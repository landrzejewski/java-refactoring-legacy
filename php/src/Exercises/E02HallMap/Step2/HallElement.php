<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step2;

use Training\Workshop\Shared\Money;

/** Krok 2: kontrakt elementu sali - bez zmian. */
interface HallElement
{
    public function capacity(): int;

    public function surcharges(): Money;
}
