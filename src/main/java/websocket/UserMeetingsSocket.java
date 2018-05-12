package websocket;

import com.google.gson.Gson;
import conf.database.MainDatabaseProps;
import gson.GSONConfiguration;
import hibernate.SessionFactoryProvider;
import io.reactivex.disposables.Disposable;
import model.user;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@WebSocket
public class UserMeetingsSocket {

	private Disposable userObserver;

	private void _initUserObserve(Session session, int user_id) {
		user userObj;
		org.hibernate.Session persistenceSession = SessionFactoryProvider.getSessionFactory(MainDatabaseProps.getDatabaseProps()).openSession();
		try {
			userObj = persistenceSession.get(user.class, user_id);
		} finally {
			persistenceSession.close();
		}
		if (userObj == null)
			throw new NotFoundException("User not found for the ID: " + user_id);

		userObserver = user.getMeetingsObservable().subscribe((u) -> {
			Map<String, Object> response = new HashMap<>();
			response.put("message", "update");
			response.put("entity", "user");
			response.put("key", user_id);
			session.getRemote().sendString(GSONConfiguration.getInstance().getConfiguration().toJson(response));
		});
	}

	@OnWebSocketConnect
	public void onConnect(Session session){
		System.out.println(session.getRemoteAddress().getHostString() + " connected!");
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
				_initUserObserve(session, Integer.parseInt(map.get("key").toString().replaceAll("\\.\\d+$", "")));
			}

			Map<String, Object> response = new HashMap<>();
			response.put("message", message);
			response.put("success", true);
			session.getRemote().sendString(gson.toJson(response));
		}
	}

	@OnWebSocketClose
	public void onClose(Session session, int status, String reason){
		if (userObserver != null && !userObserver.isDisposed())
			userObserver.dispose();
	}
}
