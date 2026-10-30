<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 1: bilet z drukarki termicznej kiosku (32 kolumny).
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
final class KioskTicketLayout implements TicketLayout
{
    /** 32 gwiazdki - pełna szerokość wydruku. */
    public const string RULE = '********************************';

    public function ticket(Sale $sale): string
    {
        return implode("\n", [
            self::RULE,
            'BILET ' . $sale->number,
            $sale->movie,
            'Miejsce ' . $sale->seat,
        ]);
    }
}
