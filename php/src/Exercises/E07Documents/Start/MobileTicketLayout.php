<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: bilet w aplikacji mobilnej - znaczniki HTML-podobne. */
final class MobileTicketLayout implements TicketLayout
{
    public function ticket(Sale $sale): string
    {
        return '<ticket><h1>' . $sale->movie . '</h1><p>Bilet ' . $sale->number
            . '</p><p>Miejsce ' . $sale->seat . '</p></ticket>';
    }
}
