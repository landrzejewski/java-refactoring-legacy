package pl.training.workshop.exercises.e01_import;

import java.util.List;

/**
 * Stabilny kontrakt ćwiczenia: nazwa pliku z repertuarem (rozszerzenie mówi o formacie:
 * {@code .csv} albo {@code .jsonl}) i jego wiersze, już wczytane do pamięci.
 */
public record ImportRequest(String fileName, List<String> lines) {
}
