<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 1: kod do kontroli biletu (kreskowy, QR albo kontrolny), zależny od kanału. */
interface CodeRenderer
{
    public function code(Sale $sale): string;
}
