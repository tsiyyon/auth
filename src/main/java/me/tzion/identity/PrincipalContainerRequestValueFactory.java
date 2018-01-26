package me.tzion.identity;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractContainerRequestValueFactory;

public class PrincipalContainerRequestValueFactory<T> extends AbstractContainerRequestValueFactory<T> {
    private Class<T> principalClass;
    public PrincipalContainerRequestValueFactory(IdentityValueFactoryProvider.PrincipalClassProvider principalClass) {
        this.principalClass = principalClass.getClazz();
    }

    @Override
    public T provide() {
        ContainerRequest containerRequest = getContainerRequest();
        Object current = containerRequest.getProperty("__current__");
        return principalClass.cast(current);
    }

    @Override
    public void dispose(T instance) {

    }
}
