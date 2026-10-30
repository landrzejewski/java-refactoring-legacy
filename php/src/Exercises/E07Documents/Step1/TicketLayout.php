<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 1: układ biletu w danym kanale. */
interface TicketLayout
{
    public function ticket(Sale $sale): string;
}
