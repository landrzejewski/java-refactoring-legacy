package pl.training.workshop.exercises.e02_hallmap;

/**
 * Stabilny kontrakt sceny: rząd o etykiecie (np. "A") i układzie miejsc, np. {@code "NN_VV"}.
 * Numer miejsca to pozycja w układzie liczona od 1, więc luka {@code _} zużywa numer.
 */
public record RowSpec(String label, String layout) {
}
