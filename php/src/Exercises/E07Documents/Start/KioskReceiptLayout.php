<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/** Start: paragon kiosku - kwoty wyrównane do prawej w 32 kolumnach. */
final class KioskReceiptLayout implements ReceiptLayout
{
    public function receipt(Sale $sale): string
    {
        $lines = [];
        $lines[] = 'PARAGON';
        $lines[] = self::row('Bilet', $sale->ticketPrice);
        if ($sale->barPrice->isGreaterThan(Money::zero())) {
            $lines[] = self::row('Bar', $sale->barPrice);
        }
        $lines[] = self::row('RAZEM', $sale->ticketPrice->plus($sale->barPrice));
        $lines[] = KioskTicketLayout::RULE;
        return implode("\n", $lines);
    }

    public function section(array $lines): string
    {
        return implode("\n", $lines) . "\n" . str_repeat('-', 32);
    }

    private static function row(string $label, Money $amount): string
    {
        return sprintf('%-22s%10s', $label, $amount);
    }
}
