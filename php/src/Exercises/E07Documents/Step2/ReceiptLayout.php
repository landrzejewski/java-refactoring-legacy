<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 2: układ paragonu i dodatkowych sekcji dokumentu w danym kanale. */
interface ReceiptLayout
{
    public function receipt(Sale $sale): string;

    /** @param list<string> $lines */
    public function section(array $lines): string;
}
