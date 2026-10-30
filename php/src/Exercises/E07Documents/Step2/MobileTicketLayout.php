<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 2: bilet w aplikacji mobilnej - znaczniki HTML-podobne.
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
final class MobileTicketLayout implements TicketLayout
{
    public function ticket(Sale $sale): string
    {
        return '<ticket><h1>' . $sale->movie . '</h1><p>Bilet ' . $sale->number
            . '</p><p>Miejsce ' . $sale->seat . '</p></ticket>';
    }
}
