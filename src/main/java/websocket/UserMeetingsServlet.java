package websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocket Servlet for user meetings", urlPatterns = { "/user/meetings" })
public class UserMeetingsServlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		// set a 10 second timeout
		factory.getPolicy().setIdleTimeout(10000);

		// register EchoSocket as the WebSocket to create on Upgrade
		factory.register(UserMeetingsSocket.class);
	}
}
