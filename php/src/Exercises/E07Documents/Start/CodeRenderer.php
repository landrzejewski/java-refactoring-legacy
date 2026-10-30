<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: kod do kontroli biletu (kreskowy, QR albo kontrolny), zależny od kanału. */
interface CodeRenderer
{
    public function code(Sale $sale): string;
}
