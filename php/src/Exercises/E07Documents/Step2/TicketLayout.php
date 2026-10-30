<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 2: układ biletu w danym kanale. */
interface TicketLayout
{
    public function ticket(Sale $sale): string;
}
