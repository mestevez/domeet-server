package websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "WebSocket Servlet for entities", urlPatterns = { "/entity" })
public class WebSocketEntityServlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		// set a 10 second timeout
		factory.getPolicy().setIdleTimeout(10000);

		// register WebSocketEntitySocket as the WebSocket to create on Upgrade
		factory.register(WebSocketEntitySocket.class);
	}
}
