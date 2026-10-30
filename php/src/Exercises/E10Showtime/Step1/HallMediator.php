<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

/**
 * Krok 1: urządzenie zgłasza, co się stało; o tym, kto i w jakiej kolejności reaguje, decyduje
 * sterownik sali. Urządzenia znają tylko ten interfejs.
 */
interface HallMediator
{
    public function doorsClosed(): void;

    public function doorsBlocked(): void;

    public function doorsCleared(): void;

    public function adsFinished(): void;
}
