package com.tsiyyon.auth;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Optional;

public class AuthenticationFilter<C, P> implements ContainerRequestFilter {
    private Credentials<C> credentials;
    private Unauthorized unauthorized;
    private Authenticator<C, P> authenticator;

    public AuthenticationFilter(Credentials<C> credentials, Authenticator<C, P> authenticator, Unauthorized unauthorized) {
        this.unauthorized = unauthorized;
        this.authenticator = authenticator;
        this.credentials = credentials;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Optional<C> credential = credentials.from(requestContext);
        if (!credential.isPresent()) {
            throw new WebApplicationException(unauthorized.handle(requestContext));
        }

        if (!authenticated(requestContext, credential.get())) {
            throw new WebApplicationException(unauthorized.handle(requestContext));
        }
    }

    private boolean authenticated(ContainerRequestContext requestContext, C credential) {
        Optional<P> authenticate = authenticator.authenticate(credential);
        if (!authenticate.isPresent()) {
            return false;
        }
        requestContext.setProperty("__current__", authenticate.get());

        return true;
    }

    public interface Unauthorized {
        Response handle(ContainerRequestContext context);
    }
}
