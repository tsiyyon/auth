package me.tzion.identity;

import java.util.Optional;

public interface Identities<Identifiable, Identity> {
    Optional<Identity> identify(Identifiable identifiable);
}
