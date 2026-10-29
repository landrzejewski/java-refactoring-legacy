package pl.training.workshop.exercises.e02_hallmap;

import java.util.List;

/** Stabilny kontrakt sceny: sektor sali, np. "Parter" albo "Balkon", z listą rzędów. */
public record SectionSpec(String name, List<RowSpec> rows) {
}
