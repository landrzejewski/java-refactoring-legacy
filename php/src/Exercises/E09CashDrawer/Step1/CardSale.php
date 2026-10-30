<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step1;

use Training\Workshop\Shared\Money;

/** Krok 1: sprzedaż biletu kartą - nie dotyka gotówki, ale trafia do eksportu fiskalnego. */
final readonly class CardSale implements DrawerEntry
{
    public function __construct(public string $reference, public Money $amount, public string $cardLast4)
    {
    }

    /**
     * @template R
     * @param DrawerEntryVisitor<R> $visitor
     * @return R
     */
    public function accept(DrawerEntryVisitor $visitor): mixed
    {
        return $visitor->visitCardSale($this);
    }
}
