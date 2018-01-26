package me.tzion.identity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Optional;

public class IdentityFilter<C, P> implements ContainerRequestFilter {
    private Identifiable<C> identifiable;
    private Unauthorized unauthorized;
    private Identities<C, P> identities;

    public IdentityFilter(Identifiable<C> identifiable, Identities<C, P> identities, Unauthorized unauthorized) {
        this.unauthorized = unauthorized;
        this.identities = identities;
        this.identifiable = identifiable;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Optional<C> identifiable = this.identifiable.from(requestContext);
        if (!identifiable.isPresent()) {
            throw new WebApplicationException(unauthorized.handle(requestContext));
        }

        if (!identified(requestContext, identifiable.get())) {
            throw new WebApplicationException(unauthorized.handle(requestContext));
        }
    }

    private boolean identified(ContainerRequestContext requestContext, C identifiable) {
        Optional<P> identity = identities.identify(identifiable);
        if (!identity.isPresent()) {
            return false;
        }
        requestContext.setProperty("__current__", identity.get());

        return true;
    }

    public interface Unauthorized {
        Response handle(ContainerRequestContext context);
    }
}
