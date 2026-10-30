<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/** Krok 2: cały rabat na ostatnim bilecie - bez zaokrągleń. Bezstanowy. */
final class LastTicketSplit implements DiscountSplit
{
    public function split(Money $discount, array $tickets): array
    {
        $shares = [];
        for ($i = 0; $i < count($tickets) - 1; $i++) {
            $shares[] = Money::zero();
        }
        $shares[] = $discount;
        return $shares;
    }
}
