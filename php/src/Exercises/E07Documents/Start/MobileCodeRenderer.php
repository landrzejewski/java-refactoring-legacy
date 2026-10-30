<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: kod QR aplikacji mobilnej. */
final class MobileCodeRenderer implements CodeRenderer
{
    public function code(Sale $sale): string
    {
        return '<qr>' . $sale->number . '</qr>';
    }
}
