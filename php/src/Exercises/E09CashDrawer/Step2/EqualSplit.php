<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/**
 * Krok 2: równe części zaokrąglone w dół; brakujące grosze dostają po jednym pierwsze bilety.
 * Bezstanowy.
 */
final class EqualSplit implements DiscountSplit
{
    private const string CENT = '0.01';

    public function split(Money $discount, array $tickets): array
    {
        $base = new Money($discount->amount->div(count($tickets), 2));
        $restCents = (int) (string) $discount->minus($base->times(count($tickets)))->amount->mul(100);
        $shares = [];
        for ($i = 0; $i < count($tickets); $i++) {
            $shares[] = $i < $restCents ? $base->plus(Money::of(self::CENT)) : $base;
        }
        return $shares;
    }
}
