package websocket;

import com.google.gson.Gson;
import gson.GSONConfiguration;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;
import model.meeting;
import model.user;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketEntitySocket {

	private Disposable observerSubscribe;

	private void _initObserve(Session session, Subject obsvervable, String entity, int key) {
		observerSubscribe = obsvervable.subscribe((u) -> {
			Map<String, Object> response = new HashMap<>();
			response.put("message", "update");
			response.put("entity", entity);
			response.put("key", key);
			session.getRemote().sendString(GSONConfiguration.getInstance().getConfiguration().toJson(response));
		});
	}

	@OnWebSocketMessage
	public void onText(Session session, String text) throws IOException {
		Gson gson = GSONConfiguration.getInstance().getConfiguration();
		Map<String, Object> map = gson.fromJson(text, Map.class);
		if (session.isOpen() && map.containsKey("message")) {
			String message = map.get("message").toString();
			if (message.equalsIgnoreCase("ping")) {
				// DO NOTHING
			} else if (message.equalsIgnoreCase("observe")) {
				String entity = map.get("entity").toString();
				int key = Integer.parseInt(map.get("key").toString().replaceAll("\\.\\d+$", ""));

				if (entity.equalsIgnoreCase("user"))
					_initObserve(session, user.getMeetingsObservable(), "user", key);
				else if (entity.equalsIgnoreCase("meeting"))
					_initObserve(session, meeting.getMeetingObservable(), "meeting", key);
			}

			Map<String, Object> response = new HashMap<>();
			response.put("message", message);
			response.put("success", true);
			session.getRemote().sendString(gson.toJson(response));
		}
	}

	@OnWebSocketClose
	public void onClose(Session session, int status, String reason){
		if (observerSubscribe != null && !observerSubscribe.isDisposed())
			observerSubscribe.dispose();
	}
}
