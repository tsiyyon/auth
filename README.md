# The jersey identity module
### usage as following
```
@Path("/")
public class Resources {
    @GET
    @Path("required")
    public Response required(
            @Current User user) {
        return Response.ok().build();
    }

    @GET
    @Path("optional")
    public Response optional(
            @Current Optional<User> user) {
        return user.map(u -> Response.ok(u.getName()).build())
                .orElse(Response.ok("anonymous").build());
    }

    @GET
    @Path("opened")
    public Response opened() {
        return Response.status(200).build();
    }**
```
1. First get need the user be authenticated. if not exception will throwed
2. Second get user can be authenticated or anonymous user.
3. The second get is opened to all the user