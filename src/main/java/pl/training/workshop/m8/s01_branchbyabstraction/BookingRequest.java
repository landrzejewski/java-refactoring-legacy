package pl.training.workshop.m8.s01_branchbyabstraction;

import java.util.List;

/**
 * Stabilny kontrakt sceny: żądanie rezerwacji.
 *
 * @param seats miejsca w formacie litera + rząd, np. "A10"
 * @param types typy biletów legacy: N, S (student), E (senior), C (dziecko)
 */
public record BookingRequest(Screening screening, List<String> seats, List<String> types,
        boolean web, boolean ownGlasses) {
}
