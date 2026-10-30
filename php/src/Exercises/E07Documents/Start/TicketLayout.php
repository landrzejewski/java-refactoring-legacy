<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: układ biletu w danym kanale. */
interface TicketLayout
{
    public function ticket(Sale $sale): string;
}
