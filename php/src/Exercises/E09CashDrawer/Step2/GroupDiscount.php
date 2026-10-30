<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/**
 * Krok 2: reguła rabatu grupowego (10+ biletów = -10% sumy) jako kontekst, który podział
 * rabatu deleguje do wstrzykniętego wariantu. Kontekst nie zna nazw trybów.
 */
final readonly class GroupDiscount
{
    private const int GROUP_SIZE = 10;
    private const int GROUP_PERCENT = 10;

    public function __construct(private DiscountSplit $split)
    {
    }

    /**
     * Rabat i jego podział na bilety; {@code null}, gdy grupa jest za mała.
     *
     * @param list<Money> $tickets
     */
    public function allocate(array $tickets): ?GroupDiscountAllocation
    {
        if (count($tickets) < self::GROUP_SIZE) {
            return null;
        }
        $sum = Money::zero();
        foreach ($tickets as $ticket) {
            $sum = $sum->plus($ticket);
        }
        $discount = $sum->percent(self::GROUP_PERCENT);
        return new GroupDiscountAllocation($discount, $this->split->split($discount, $tickets));
    }
}
