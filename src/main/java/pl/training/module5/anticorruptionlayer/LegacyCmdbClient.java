package pl.training.module5.anticorruptionlayer;

import java.util.List;

/**
 * Zewnętrzny system, którego modelu nie kontrolujemy.
 */
@FunctionalInterface
public interface LegacyCmdbClient {
    List<LegacyCmdbRecord> fetchAll();
}
