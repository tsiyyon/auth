package com.tsiyyon.auth;

import java.util.Optional;

public interface Authenticator<C, P> {
    Optional<P> authenticate(C credential);
}
