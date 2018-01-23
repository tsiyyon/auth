package com.tsiyyon.auth;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

public interface Credentials<C> {
    Optional<C> from(ContainerRequestContext context);
}
