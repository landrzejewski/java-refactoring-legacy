package pl.training.workshop.exercises.e02_hallmap

/** Stabilny kontrakt sceny: sektor sali, np. "Parter" albo "Balkon", z listą rzędów. */
data class SectionSpec(val name: String, val rows: List<RowSpec>)
