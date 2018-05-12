package websocket;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "MyEcho WebSocket Servlet", urlPatterns = { "/echo" })
public class EchoServlet extends WebSocketServlet {

	@Override
	public void configure(WebSocketServletFactory factory) {
		// set a 10 second timeout
		factory.getPolicy().setIdleTimeout(10000);

		// register EchoSocket as the WebSocket to create on Upgrade
		factory.register(EchoSocket.class);
	}
}
