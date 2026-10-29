package pl.training.workshop.exercises.e06_movieinfo.step2;

import pl.training.workshop.exercises.e06_movieinfo.MovieDetails;
import pl.training.workshop.exercises.e06_movieinfo.MovieSource;
import pl.training.workshop.exercises.e06_movieinfo.User;

/**
 * Krok 2: strażnik dostępu jako zastępca serwisu. Reguła "tylko personel" jest w jednym miejscu,
 * a odrzucone pytanie nigdy nie dociera do serwisu ani do źródeł danych.
 */
public final class AuthorizingLookup implements MovieLookup {
    private final MovieLookup target;

    public AuthorizingLookup(MovieLookup target) {
        this.target = target;
    }

    @Override
    public MovieDetails lookup(User user, MovieSource source, String id) {
        requireStaff(user);
        return target.lookup(user, source, id);
    }

    @Override
    public int runtime(User user, MovieSource source, String id) {
        requireStaff(user);
        return target.runtime(user, source, id);
    }

    private static void requireStaff(User user) {
        if (!user.staff()) {
            throw new IllegalStateException("brak uprawnien: " + user.name());
        }
    }
}
