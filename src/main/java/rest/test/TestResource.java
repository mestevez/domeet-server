package rest.test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/testresource")
public class TestResource {

	@GET
	@Produces("text/plain")
	public String hello() {
		return "Hello World!";
	}
}
