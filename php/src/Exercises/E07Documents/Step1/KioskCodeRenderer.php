<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 1: kod kreskowy kiosku - czytnik przy wejściu na salę.
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
final class KioskCodeRenderer implements CodeRenderer
{
    public function code(Sale $sale): string
    {
        return '|| ||| | ' . $sale->number . ' | ||| ||';
    }
}
