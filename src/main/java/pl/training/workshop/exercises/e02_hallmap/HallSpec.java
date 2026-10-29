package pl.training.workshop.exercises.e02_hallmap;

import java.util.List;

/**
 * Stabilny kontrakt sceny: plan sali z systemu rezerwacji. Sala ma nazwę i sektory
 * (np. "Parter", "Balkon"), sektor ma rzędy, a rząd ma etykietę (np. "A") i układ miejsc
 * zapisany znakami: {@code N} normalne, {@code V} VIP (+10.00), {@code W} miejsce dla osoby
 * na wózku, {@code D} dwuosobowe (+6.00), {@code _} przejście (luka, która zużywa numer).
 */
public record HallSpec(String name, List<SectionSpec> sections) {
}
