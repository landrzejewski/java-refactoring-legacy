<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/**
 * Krok 1: paragon fiskalny A4 z wykropkowaniem do kwoty.
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
final class BoxOfficeReceiptLayout implements ReceiptLayout
{
    public function receipt(Sale $sale): string
    {
        $lines = [];
        $lines[] = 'PARAGON FISKALNY';
        $lines[] = self::row('Bilet', $sale->ticketPrice);
        if ($sale->barPrice->isGreaterThan(Money::zero())) {
            $lines[] = self::row('Bar', $sale->barPrice);
        }
        $lines[] = self::row('RAZEM', $sale->ticketPrice->plus($sale->barPrice));
        return implode("\n", $lines);
    }

    public function section(array $lines): string
    {
        return implode("\n", array_map(fn (string $line) => '  ' . $line, $lines));
    }

    private static function row(string $label, Money $amount): string
    {
        return $label . ' ' . str_repeat('.', 18 - strlen($label)) . ' ' . $amount;
    }
}
