<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications;

/**
 * Stabilny kontrakt sceny: jeden seans, posiadacze biletow, lista oczekujacych i kolejne zmiany.
 * Wynikiem przebiegu jest dziennik efektow: wpis repertuaru, powiadomienia (w kolejnosci klientow),
 * reakcja listy oczekujacych i statystyki. Blad walidacji konczy przebieg wpisem {@code ERROR: ...}.
 */
final readonly class Scenario
{
    /**
     * @param list<Customer> $ticketHolders
     * @param list<Customer> $waiting
     * @param list<Change> $changes
     */
    public function __construct(
        public Screening $screening,
        public array $ticketHolders,
        public array $waiting,
        public array $changes,
    ) {
    }
}
