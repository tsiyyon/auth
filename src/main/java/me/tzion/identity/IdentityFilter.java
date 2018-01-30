package me.tzion.identity;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.util.Optional;

public class IdentityFilter<Identifiable, Identity> implements ContainerRequestFilter {
    private me.tzion.identity.Identifiable<Identifiable> identifiable;
    private Unauthorized unauthorized;
    private Identities<Identifiable, Identity> identities;

    public IdentityFilter(me.tzion.identity.Identifiable<Identifiable> identifiable, Identities<Identifiable, Identity> identities, Unauthorized unauthorized) {
        this.unauthorized = unauthorized;
        this.identities = identities;
        this.identifiable = identifiable;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) {
        Optional<Identifiable> identifiable = this.identifiable.from(requestContext);
        if (!identifiable.isPresent()) {
            throw new WebApplicationException(unauthorized.handle(requestContext));
        }

        if (!identified(requestContext, identifiable.get())) {
            throw new WebApplicationException(unauthorized.handle(requestContext));
        }
    }

    private boolean identified(ContainerRequestContext requestContext, Identifiable identifiable) {
        Optional<Identity> identity = identities.identify(identifiable);
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
