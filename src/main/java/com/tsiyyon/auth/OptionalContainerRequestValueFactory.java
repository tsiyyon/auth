package com.tsiyyon.auth;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;

import java.util.Optional;

public class OptionalContainerRequestValueFactory<T> extends AbstractContainerRequestValueFactory<Optional<T>> {
    private Class<T> principalClass;
    public OptionalContainerRequestValueFactory(AuthValueFactoryProvider.PrincipalClassProvider principalClass) {
        this.principalClass = principalClass.getClazz();
    }
    @Override
    public Optional<T> provide() {
        ContainerRequest containerRequest = getContainerRequest();
        Object current = containerRequest.getProperty("__current__");
        return Optional.ofNullable(principalClass.cast(current));
    }
}
