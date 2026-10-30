<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step1;

/**
 * Krok 1: powiadamianie klientow wyjete ze schedulera do pierwszego odbiorcy zdarzen.
 * Lancuch if wyboru kanalu i szesc klas powiadomien przeniosly sie tu bez zmian - to temat kolejnych krokow.
 */
final class CustomerNotifier implements ScreeningListener
{
    public function __construct(private readonly Journal $journal)
    {
    }

    public function screeningMoved(ScreeningMoved $event): void
    {
        foreach ($event->ticketHolders as $customer) {
            if ($customer->pushToken !== null && trim($customer->pushToken) !== ''
                    && !str_starts_with($customer->pushToken, 'expired-')) {
                (new PushMovedNotice($this->journal, $event->before, $event->after))->send($customer);
            } elseif ($customer->phone !== null && trim($customer->phone) !== '') {
                (new SmsMovedNotice($this->journal, $event->before, $event->after))->send($customer);
            } elseif ($customer->email !== null && trim($customer->email) !== '') {
                (new EmailMovedNotice($this->journal, $event->before, $event->after))->send($customer);
            } else {
                $this->journal->note('UNDELIVERED ' . $customer->name . ' (brak kanalu)');
            }
        }
    }

    public function screeningCancelled(ScreeningCancelled $event): void
    {
        foreach ($event->ticketHolders as $customer) {
            if ($customer->pushToken !== null && trim($customer->pushToken) !== ''
                    && !str_starts_with($customer->pushToken, 'expired-')) {
                (new PushCancelledNotice($this->journal, $event->screening, $event->reason))->send($customer);
            } elseif ($customer->phone !== null && trim($customer->phone) !== '') {
                (new SmsCancelledNotice($this->journal, $event->screening, $event->reason))->send($customer);
            } elseif ($customer->email !== null && trim($customer->email) !== '') {
                (new EmailCancelledNotice($this->journal, $event->screening, $event->reason))->send($customer);
            } else {
                $this->journal->note('UNDELIVERED ' . $customer->name . ' (brak kanalu)');
            }
        }
    }
}
