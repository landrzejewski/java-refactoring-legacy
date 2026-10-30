<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Start;

use Training\Workshop\Shared\Money;

/** Start: sprzedaż biletu kartą - nie dotyka gotówki, ale trafia do eksportu fiskalnego. */
final readonly class CardSale implements DrawerEntry
{
    public function __construct(public string $reference, public Money $amount, public string $cardLast4)
    {
    }
}
