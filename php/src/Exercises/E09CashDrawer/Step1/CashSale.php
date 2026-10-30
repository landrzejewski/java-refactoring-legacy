<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step1;

use Training\Workshop\Shared\Money;

/** Krok 1: sprzedaż biletu za gotówkę - zasila szufladę. */
final readonly class CashSale implements DrawerEntry
{
    public function __construct(public string $reference, public Money $amount)
    {
    }

    /**
     * @template R
     * @param DrawerEntryVisitor<R> $visitor
     * @return R
     */
    public function accept(DrawerEntryVisitor $visitor): mixed
    {
        return $visitor->visitCashSale($this);
    }
}
