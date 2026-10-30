<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: bilet A4 drukowany w kasie. */
final class BoxOfficeTicketLayout implements TicketLayout
{
    public function ticket(Sale $sale): string
    {
        return implode("\n", [
            'CINELEGACY - BILET WSTEPU',
            'Numer:   ' . $sale->number,
            'Film:    ' . $sale->movie,
            'Miejsce: ' . $sale->seat,
        ]);
    }
}
