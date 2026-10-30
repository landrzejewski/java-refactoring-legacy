<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: kod kreskowy kiosku - czytnik przy wejściu na salę. */
final class KioskCodeRenderer implements CodeRenderer
{
    public function code(Sale $sale): string
    {
        return '|| ||| | ' . $sale->number . ' | ||| ||';
    }
}
