package me.tzion.identity;

import javax.inject.Singleton;

@Singleton
public class IdentityTypeProvider<T> {
    private final Class<T> clazz;

    public IdentityTypeProvider(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
