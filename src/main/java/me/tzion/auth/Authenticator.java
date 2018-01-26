package me.tzion.auth;

import java.util.Optional;

public interface Authenticator<C, P> {
    Optional<P> authenticate(C credential);
}
