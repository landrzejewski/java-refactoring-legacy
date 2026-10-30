<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: bilet z drukarki termicznej kiosku (32 kolumny). */
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
