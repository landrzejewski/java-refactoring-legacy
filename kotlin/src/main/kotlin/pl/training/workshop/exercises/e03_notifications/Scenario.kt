package pl.training.workshop.exercises.e03_notifications

/**
 * Stabilny kontrakt sceny: jeden seans, posiadacze biletow, lista oczekujacych i kolejne zmiany.
 * Wynikiem przebiegu jest dziennik efektow: wpis repertuaru, powiadomienia (w kolejnosci klientow),
 * reakcja listy oczekujacych i statystyki. Blad walidacji konczy przebieg wpisem `ERROR: ...`.
 */
data class Scenario(val screening: Screening, val ticketHolders: List<Customer>, val waiting: List<Customer>, val changes: List<Change>)
