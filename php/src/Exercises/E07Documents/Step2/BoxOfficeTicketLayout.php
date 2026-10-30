<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 2: bilet A4 drukowany w kasie.
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
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
