package websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;


@WebSocket
public class EchoSocket {

	@OnWebSocketConnect
	public void onConnect(Session session){
		System.out.println(session.getRemoteAddress().getHostString() + " connected!");
	}

	@OnWebSocketMessage
	public void onText(Session session, String message) throws IOException {
		System.out.println("Message received:" + message);
		if (session.isOpen()) {
			String response = message.toUpperCase();
			session.getRemote().sendString(response);
		}
	}

	@OnWebSocketClose
	public void onClose(Session session, int status, String reason){
		System.out.println(session.getRemoteAddress().getHostString() + " closed!");
	}



}
