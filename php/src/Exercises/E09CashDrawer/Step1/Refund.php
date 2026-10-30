<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step1;

use Training\Workshop\Shared\Money;

/** Krok 1: zwrot gotówki za bilet - pomniejsza szufladę, w eksporcie fiskalnym kwota ujemna. */
final readonly class Refund implements DrawerEntry
{
    public function __construct(public string $reference, public Money $amount, public string $reason)
    {
    }

    /**
     * @template R
     * @param DrawerEntryVisitor<R> $visitor
     * @return R
     */
    public function accept(DrawerEntryVisitor $visitor): mixed
    {
        return $visitor->visitRefund($this);
    }
}
