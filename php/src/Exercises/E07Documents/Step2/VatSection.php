<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Shared\Money;

/** Krok 2: sekcja VAT (bilety 8%, bar 23%) dopisana pod dokumentem wewnętrznym. */
final readonly class VatSection implements Document
{
    public function __construct(public Document $inner, public ReceiptLayout $layout, public Sale $sale)
    {
    }

    public function render(): string
    {
        $lines = [];
        $lines[] = 'VAT 8% bilet: ' . self::vat($this->sale->ticketPrice, 8);
        if ($this->sale->barPrice->isGreaterThan(Money::zero())) {
            $lines[] = 'VAT 23% bar: ' . self::vat($this->sale->barPrice, 23);
        }
        return $this->inner->render() . "\n" . $this->layout->section($lines);
    }

    /** VAT zawarty w cenie brutto, np. 8% z 40.00 to 2.96. */
    private static function vat(Money $gross, int $rate): Money
    {
        return new Money($gross->amount->mul($rate)->div(100 + $rate, 10));
    }
}
