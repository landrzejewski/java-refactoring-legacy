<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Start: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
interface ReceiptLayout
{
    public function receipt(Sale $sale): string;

    /** @param list<string> $lines */
    public function section(array $lines): string;
}
