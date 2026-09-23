package pl.training.workshop.m8.s10_qualitygate;

import java.nio.file.Path;

/**
 * Stabilny kontrakt sceny: co bramka ma sprawdzić.
 *
 * @param sources    katalog ze źródłami domeny (bez podkatalogów)
 * @param testSource plik testu kluczowej klasy
 * @param keyClass   prosta nazwa kluczowej klasy domeny, np. PriceTable
 * @param testClass  pełna nazwa klasy testowej do uruchomienia
 */
public record GateInput(Path sources, Path testSource, String keyClass, String testClass) {
}
