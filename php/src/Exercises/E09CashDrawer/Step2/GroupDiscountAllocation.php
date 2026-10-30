<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/**
 * Krok 2: wynik reguły rabatu grupowego - kwota rabatu i jej podział na bilety.
 * Odpowiednik zagnieżdżonego rekordu {@code GroupDiscount.Allocation} z Javy (PHP nie ma klas zagnieżdżonych).
 */
final readonly class GroupDiscountAllocation
{
    /** @param list<Money> $shares */
    public function __construct(public Money $discount, public array $shares)
    {
    }
}
