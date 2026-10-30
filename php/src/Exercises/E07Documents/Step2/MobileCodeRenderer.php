<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 2: kod QR aplikacji mobilnej.
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
final class MobileCodeRenderer implements CodeRenderer
{
    public function code(Sale $sale): string
    {
        return '<qr>' . $sale->number . '</qr>';
    }
}
