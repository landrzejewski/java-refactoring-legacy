package pl.training.module5.boundaryobject;

import java.util.Map;

/**
 * Zewnętrzny system CI: zwraca surowy, nietypowany ładunek
 * (klucze {@code id, status, coverage, failed, commit}, wszystkie wartości jako tekst).
 */
@FunctionalInterface
public interface LegacyCiClient {
    Map<String, String> fetchBuild(String buildId);
}
