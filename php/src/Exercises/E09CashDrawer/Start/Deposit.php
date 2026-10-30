<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Start;

use Training\Workshop\Shared\Money;

/** Start: pogotowie kasowe (wpłata do szuflady) - gotówka, ale bez linii fiskalnej. */
final readonly class Deposit implements DrawerEntry
{
    public function __construct(public Money $amount, public string $note)
    {
    }
}
