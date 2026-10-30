<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step1;

use Training\Workshop\Shared\Money;

/** Krok 1: pogotowie kasowe (wpłata do szuflady) - gotówka, ale bez linii fiskalnej. */
final readonly class Deposit implements DrawerEntry
{
    public function __construct(public Money $amount, public string $note)
    {
    }

    /**
     * @template R
     * @param DrawerEntryVisitor<R> $visitor
     * @return R
     */
    public function accept(DrawerEntryVisitor $visitor): mixed
    {
        return $visitor->visitDeposit($this);
    }
}
