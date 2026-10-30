<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/**
 * Krok 1: scheduler zapisuje zmiane i publikuje zdarzenie do zarejestrowanych odbiorcow.
 * Kolejnosc powiadamiania to kolejnosc rejestracji - to czesc kontraktu (powiadomienia, lista, statystyki).
 * Wyjatek odbiorcy przerywa powiadamianie kolejnych - tak bylo w start i tak zostaje.
 *
 * Różnica względem Javy: foreach po tablicy odbiorcow dziala na jej kopii, wiec odpowiednik
 * {@code List.copyOf(listeners)} jest zbedny - wypisanie sie w trakcie powiadamiania niczego nie psuje.
 */
final class ScreeningScheduler
{
    /** @var list<ScreeningListener> */
    private array $listeners = [];
    /** @var array<string, Screening> */
    private array $current = [];
    /** @var array<string, true> */
    private array $cancelled = [];

    public function __construct(private readonly Journal $journal)
    {
    }

    public function subscribe(ScreeningListener $listener): void
    {
        $this->listeners[] = $listener;
    }

    public function unsubscribe(ScreeningListener $listener): void
    {
        $index = array_search($listener, $this->listeners, true);
        if ($index !== false) {
            array_splice($this->listeners, $index, 1);
        }
    }

    public function register(Screening $screening): void
    {
        $this->current[$screening->id] = $screening;
    }

    /** @param list<Customer> $ticketHolders */
    public function move(string $screeningId, ?string $newStart, ?string $newHall, array $ticketHolders): void
    {
        if ($newStart === null || trim($newStart) === '') {
            throw new InvalidArgumentException('new start must not be blank');
        }
        if ($newHall === null || trim($newHall) === '') {
            throw new InvalidArgumentException('hall must not be blank');
        }
        $before = $this->live($screeningId);
        $after = new Screening($before->id, $before->title, $newStart, $newHall);
        $this->current[$screeningId] = $after;
        $this->journal->note('SCHEDULE ' . $before->title . ' ' . $before->start . ' -> ' . $newStart . ' sala ' . $newHall);
        $event = new ScreeningMoved($before, $after, $ticketHolders);
        foreach ($this->listeners as $listener) {
            $listener->screeningMoved($event);
        }
    }

    /** @param list<Customer> $ticketHolders */
    public function cancel(string $screeningId, ?string $reason, array $ticketHolders): void
    {
        if ($reason === null || trim($reason) === '') {
            throw new InvalidArgumentException('reason must not be blank');
        }
        $screening = $this->live($screeningId);
        $this->cancelled[$screeningId] = true;
        $this->journal->note('SCHEDULE ' . $screening->title . ' ' . $screening->start . ' odwolany: ' . $reason);
        $event = new ScreeningCancelled($screening, $reason, $ticketHolders);
        foreach ($this->listeners as $listener) {
            $listener->screeningCancelled($event);
        }
    }

    private function live(string $screeningId): Screening
    {
        $screening = $this->current[$screeningId] ?? null;
        if ($screening === null) {
            throw new InvalidArgumentException('unknown screening: ' . $screeningId);
        }
        if (isset($this->cancelled[$screeningId])) {
            throw new InvalidArgumentException('screening already cancelled: ' . $screening->title);
        }
        return $screening;
    }
}
