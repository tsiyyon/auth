package me.tzion.auth.support;

import me.tzion.auth.Auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Optional;

@Path("/")
public class Resources {
    @GET
    @Path("required")
    public Response required(
            @Auth User user) {
        return Response.ok().build();
    }

    @GET
    @Path("optional")
    public Response optional(
            @Auth Optional<User> user) {
        return user.map(u -> Response.ok(u.getName()).build())
                .orElse(Response.ok("anonymous").build());
    }

    @GET
    @Path("opened")
    public Response opened() {
        return Response.status(200).build();
    }
}
