<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E03Notifications\Customer;
use Training\Workshop\Exercises\E03Notifications\Screening;

/**
 * Start: zmiana repertuaru sama wie, kogo i jak powiadomic. Po kazdej zmianie scheduler na sztywno
 * wola kolejno: powiadomienia klientow, liste oczekujacych i statystyki. Wybor kanalu (push, potem SMS,
 * potem e-mail) jest lancuchem if skopiowanym do obu metod, a kazda para kanal x rodzaj to osobna klasa.
 * Nowy odbiorca zmian (np. kasa biletowa) albo nowy kanal to edycja tej klasy w dwoch miejscach.
 */
final class ScreeningScheduler
{
    /** @var array<string, Screening> */
    private array $current = [];
    /** @var array<string, true> */
    private array $cancelled = [];

    public function __construct(
        private readonly Journal $journal,
        private readonly Waitlist $waitlist,
        private readonly ChangeStats $stats,
    ) {
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
        foreach ($ticketHolders as $customer) {
            if ($customer->pushToken !== null && trim($customer->pushToken) !== ''
                    && !str_starts_with($customer->pushToken, 'expired-')) {
                (new PushMovedNotice($this->journal, $before, $after))->send($customer);
            } elseif ($customer->phone !== null && trim($customer->phone) !== '') {
                (new SmsMovedNotice($this->journal, $before, $after))->send($customer);
            } elseif ($customer->email !== null && trim($customer->email) !== '') {
                (new EmailMovedNotice($this->journal, $before, $after))->send($customer);
            } else {
                $this->journal->note('UNDELIVERED ' . $customer->name . ' (brak kanalu)');
            }
        }
        $this->waitlist->screeningMoved($after);
        $this->stats->moved();
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
        foreach ($ticketHolders as $customer) {
            if ($customer->pushToken !== null && trim($customer->pushToken) !== ''
                    && !str_starts_with($customer->pushToken, 'expired-')) {
                (new PushCancelledNotice($this->journal, $screening, $reason))->send($customer);
            } elseif ($customer->phone !== null && trim($customer->phone) !== '') {
                (new SmsCancelledNotice($this->journal, $screening, $reason))->send($customer);
            } elseif ($customer->email !== null && trim($customer->email) !== '') {
                (new EmailCancelledNotice($this->journal, $screening, $reason))->send($customer);
            } else {
                $this->journal->note('UNDELIVERED ' . $customer->name . ' (brak kanalu)');
            }
        }
        $this->waitlist->screeningCancelled($screening);
        $this->stats->cancelled();
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
