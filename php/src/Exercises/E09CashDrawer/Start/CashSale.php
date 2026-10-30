<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Start;

use Training\Workshop\Shared\Money;

/** Start: sprzedaż biletu za gotówkę - zasila szufladę. */
final readonly class CashSale implements DrawerEntry
{
    public function __construct(public string $reference, public Money $amount)
    {
    }
}
