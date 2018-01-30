package me.tzion.identity;

import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.jersey.server.ContainerRequest;

import javax.inject.Inject;
import javax.inject.Provider;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

public class IdentityResolver<T> implements InjectionResolver<Current> {
    @Inject
    private IdentityTypeProvider<T> principalClass;

    @Inject
    private Provider<ContainerRequest> request;

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> root) {
        if (injectee.getRequiredType() == principalClass.getClazz()) {
            ContainerRequest containerRequest = request.get();
            Object current = containerRequest.getProperty("__current__");
            return principalClass.getClazz().cast(current);
        } else if (injectee.getRequiredType() instanceof ParameterizedType && ((ParameterizedType) injectee.getRequiredType()).getRawType() == Optional.class && ((ParameterizedType) injectee.getRequiredType()).getActualTypeArguments()[0] == principalClass.getClazz()) {
            ContainerRequest containerRequest = request.get();
            Object current = containerRequest.getProperty("__current__");
            return Optional.ofNullable(principalClass.getClazz().cast(current));
        }

        return null;
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return true;
    }
}
