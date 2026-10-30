<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/**
 * Krok 2: paragon w aplikacji mobilnej.
 *
 * @internal tylko dla klas tej przestrzeni nazw (w Javie klasa pakietowa, PHP nie ma takiej widoczności)
 */
final class MobileReceiptLayout implements ReceiptLayout
{
    public function receipt(Sale $sale): string
    {
        $out = '<receipt>';
        $out .= '<li>Bilet ' . $sale->ticketPrice . '</li>';
        if ($sale->barPrice->isGreaterThan(Money::zero())) {
            $out .= '<li>Bar ' . $sale->barPrice . '</li>';
        }
        $out .= '<li><b>Razem ' . $sale->ticketPrice->plus($sale->barPrice) . '</b></li>';
        return $out . '</receipt>';
    }

    public function section(array $lines): string
    {
        return '<section><p>' . implode('</p><p>', $lines) . '</p></section>';
    }
}
