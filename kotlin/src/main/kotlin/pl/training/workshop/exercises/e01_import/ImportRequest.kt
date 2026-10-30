package pl.training.workshop.exercises.e01_import

/**
 * Stabilny kontrakt ćwiczenia: nazwa pliku z repertuarem (rozszerzenie mówi o formacie:
 * `.csv` albo `.jsonl`) i jego wiersze, już wczytane do pamięci.
 */
data class ImportRequest(val fileName: String, val lines: List<String>)
