package pl.training.workshop.exercises.e03_notifications;

import java.util.List;

/**
 * Stabilny kontrakt sceny: jeden seans, posiadacze biletow, lista oczekujacych i kolejne zmiany.
 * Wynikiem przebiegu jest dziennik efektow: wpis repertuaru, powiadomienia (w kolejnosci klientow),
 * reakcja listy oczekujacych i statystyki. Blad walidacji konczy przebieg wpisem {@code ERROR: ...}.
 */
public record Scenario(Screening screening, List<Customer> ticketHolders, List<Customer> waiting, List<Change> changes) {
}
