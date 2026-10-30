<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/**
 * Krok 2: kontrakt podziału rabatu grupowego na bilety. Wynik ma tyle pozycji, ile biletów,
 * a jego suma to dokładnie kwota rabatu - zaokrąglenie jest sprawą każdego wariantu.
 */
interface DiscountSplit
{
    /**
     * @param list<Money> $tickets
     * @return list<Money>
     */
    public function split(Money $discount, array $tickets): array;
}
