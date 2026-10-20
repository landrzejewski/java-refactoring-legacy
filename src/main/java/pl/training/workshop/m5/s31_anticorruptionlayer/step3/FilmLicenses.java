package pl.training.workshop.m5.s31_anticorruptionlayer.step3;

import java.util.Optional;

/** Krok 3: port domeny - skąd kino bierze licencje, opisane w języku kina (brak = Optional.empty, nie null). */
@FunctionalInterface
public interface FilmLicenses {
    Optional<FilmLicense> find(String filmCode);
}
