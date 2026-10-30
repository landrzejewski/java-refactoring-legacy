<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/** Start: paragon w aplikacji mobilnej. */
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
