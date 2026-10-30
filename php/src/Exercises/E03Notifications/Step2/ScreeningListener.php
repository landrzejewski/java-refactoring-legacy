<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

/**
 * Krok 2: wspolny interfejs odbiorcow zmian repertuaru. Scheduler zna tylko ten typ,
 * a nie klasy powiadomien, listy oczekujacych ani statystyk.
 */
interface ScreeningListener
{
    public function screeningMoved(ScreeningMoved $event): void;

    public function screeningCancelled(ScreeningCancelled $event): void;
}
