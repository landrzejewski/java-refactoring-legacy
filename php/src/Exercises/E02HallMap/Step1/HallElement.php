<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step1;

use Training\Workshop\Shared\Money;

/**
 * Krok 1: wspólny kontrakt każdego elementu sali - od pojedynczego miejsca po całą salę.
 * Klient pyta o pojemność i dopłaty bez wiedzy, ile poziomów kryje się w środku.
 */
interface HallElement
{
    public function capacity(): int;

    public function surcharges(): Money;
}
