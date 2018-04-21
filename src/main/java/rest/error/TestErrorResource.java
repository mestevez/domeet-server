package rest.error;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/test")
public class TestErrorResource {

	@Path("/unauthorized")
	@GET
	public Response unauthorizedError() {
		if (true)
			throw new NotAuthorizedException("TEST");
		return Response.accepted().build();
	}

	@Path("/unhandled")
	@GET
	public Response unhandledError() throws Exception {
		if (true)
			throw new Exception("Unhandled exception");
		return Response.accepted().build();
	}

	@Path("/unhandled")
	@POST
	public Response unhandledErrorOnForm() {
		if (true)
			throw new WebApplicationException("Unhandled exception");
		return Response.accepted().build();
	}
}
