package me.tzion.identity;

import javax.ws.rs.container.ContainerRequestContext;
import java.util.Optional;

public interface Identifiable<C> {
    Optional<C> from(ContainerRequestContext context);
}
