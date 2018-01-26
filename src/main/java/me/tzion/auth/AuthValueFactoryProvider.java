package me.tzion.auth;

import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.internal.inject.AbstractValueFactoryProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueFactoryProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;

@Singleton
public class AuthValueFactoryProvider<T> extends AbstractValueFactoryProvider {
    private final PrincipalClassProvider<T> cl;
    private Class<T> principalClass;

    @Inject
    protected AuthValueFactoryProvider(MultivaluedParameterExtractorProvider mpep,
                                       ServiceLocator locator,
                                       PrincipalClassProvider<T> principalClass) {
        super(mpep, locator, Parameter.Source.UNKNOWN);
        this.principalClass = principalClass.clazz;
        this.cl = principalClass;
    }

    @Override
    protected Factory<?> createValueFactory(Parameter parameter) {
        if (!parameter.isAnnotationPresent(Auth.class)) {
            return null;
        } else if (principalClass.equals(parameter.getRawType())) {
            return new PrincipalContainerRequestValueFactory<>(cl);
        } else {
            boolean optional = parameter.getRawType() == Optional.class
                    && ParameterizedType.class.isAssignableFrom(parameter.getType().getClass())
                    && ((ParameterizedType) parameter.getType()).getActualTypeArguments()[0] == principalClass;
            return optional ? new OptionalContainerRequestValueFactory(cl) : null;
        }
    }

    @Singleton
    public static class PrincipalClassProvider<T> {
        private final Class<T> clazz;

        private PrincipalClassProvider(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Class<T> getClazz() {
            return clazz;
        }
    }

    @Singleton
    private static class AuthInjectionResolver extends ParamInjectionResolver {
        public AuthInjectionResolver() {
            super(AuthValueFactoryProvider.class);
        }
    }

    public static class Binder<T> extends AbstractBinder {
        private Class<T> principalClass;

        public Binder(Class<T> principalClass) {
            this.principalClass = principalClass;
        }

        @Override
        protected void configure() {
            bind(new PrincipalClassProvider<>(principalClass)).to(PrincipalClassProvider.class);
            bind(AuthValueFactoryProvider.class).to(ValueFactoryProvider.class).in(Singleton.class);
            bind(AuthInjectionResolver.class)
                    .to(new TypeLiteral<InjectionResolver<Auth>>() {
                    })
                    .in(Singleton.class);
        }
    }
}
