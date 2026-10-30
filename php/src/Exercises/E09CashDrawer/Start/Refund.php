<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Start;

use Training\Workshop\Shared\Money;

/** Start: zwrot gotówki za bilet - pomniejsza szufladę, w eksporcie fiskalnym kwota ujemna. */
final readonly class Refund implements DrawerEntry
{
    public function __construct(public string $reference, public Money $amount, public string $reason)
    {
    }
}
