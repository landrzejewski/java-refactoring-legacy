<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

/**
 * Krok 2: powiadamianie nie wybiera juz kanalu - dostaje poczatek lancucha dostarczania jako zwykly
 * kanal. Nie ma tu zadnego if po danych kontaktowych ani wpisu o niedostarczeniu.
 * Tresc kazdego rodzaju wiadomosci jest raz, a sposob wysylki (np. skracanie SMS) zostaje po stronie kanalu.
 */
final class CustomerNotifier implements ScreeningListener
{
    public function __construct(private readonly Channel $delivery)
    {
    }

    public function screeningMoved(ScreeningMoved $event): void
    {
        $subject = 'Zmiana seansu: ' . $event->after->title;
        $body = 'Seans ' . $event->after->title . ' z ' . $event->before->start . ' przeniesiony na '
            . $event->after->start . ', sala ' . $event->after->hall . '. Bilety zachowuja waznosc.';
        foreach ($event->ticketHolders as $customer) {
            $this->delivery->deliver($customer, $subject, $body);
        }
    }

    public function screeningCancelled(ScreeningCancelled $event): void
    {
        $subject = 'Odwolany seans: ' . $event->screening->title;
        $body = 'Seans ' . $event->screening->title . ' z ' . $event->screening->start . ' odwolany ('
            . $event->reason . '). Zwrot w ciagu 7 dni.';
        foreach ($event->ticketHolders as $customer) {
            $this->delivery->deliver($customer, $subject, $body);
        }
    }
}
