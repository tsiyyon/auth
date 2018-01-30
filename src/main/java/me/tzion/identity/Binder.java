package me.tzion.identity;

import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class Binder<T> extends AbstractBinder {
    private Class<T> principalClass;

    public Binder(Class<T> principalClass) {
        this.principalClass = principalClass;
    }

    @Override
    protected void configure() {

        bind(new IdentityTypeProvider<>(principalClass)).to(IdentityTypeProvider.class);
        bind(IdentityResolver.class)
                .to(new TypeLiteral<InjectionResolver<Current>>() {
                })
                .in(Singleton.class);
    }

}
