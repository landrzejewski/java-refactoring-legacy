<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 2: kod do kontroli biletu (kreskowy, QR albo kontrolny), zależny od kanału. */
interface CodeRenderer
{
    public function code(Sale $sale): string;
}
