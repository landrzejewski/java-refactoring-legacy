<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/**
 * Krok 2: udział proporcjonalny do ceny biletu, zaokrąglony HALF_UP; różnica zaokrągleń
 * trafia na ostatni bilet, żeby suma zgadzała się co do grosza. Bezstanowy.
 */
final class ProportionalSplit implements DiscountSplit
{
    public function split(Money $discount, array $tickets): array
    {
        $sum = Money::zero();
        foreach ($tickets as $ticket) {
            $sum = $sum->plus($ticket);
        }
        $shares = [];
        $given = Money::zero();
        for ($i = 0; $i < count($tickets) - 1; $i++) {
            $share = new Money($discount->amount->mul($tickets[$i]->amount)->div($sum->amount, 10));
            $shares[] = $share;
            $given = $given->plus($share);
        }
        $shares[] = $discount->minus($given);
        return $shares;
    }
}
