<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

/**
 * Krok 2: urządzenie zgłasza, co się stało; o tym, kto i w jakiej kolejności reaguje, decyduje
 * sterownik sali. Urządzenia znają tylko ten interfejs.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
interface HallMediator
{
    public function doorsClosed(): void;

    public function doorsBlocked(): void;

    public function doorsCleared(): void;

    public function adsFinished(): void;
}
