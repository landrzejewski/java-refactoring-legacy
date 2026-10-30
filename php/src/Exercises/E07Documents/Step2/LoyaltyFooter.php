<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 2: stopka lojalnościowa - 1 punkt za każde pełne 10.00 za bilet, 100 punktów = darmowy bilet 2D. */
final readonly class LoyaltyFooter implements Document
{
    public function __construct(public Document $inner, public ReceiptLayout $layout, public Sale $sale)
    {
    }

    public function render(): string
    {
        $earned = intdiv((int) (string) $this->sale->ticketPrice->amount, 10);
        $total = $this->sale->loyaltyPoints + $earned;
        $lines = [];
        $lines[] = 'Punkty: ' . $this->sale->loyaltyPoints . ' + ' . $earned . ' = ' . $total;
        if ($total >= 100) {
            $lines[] = 'Odbierz darmowy bilet 2D';
        }
        return $this->inner->render() . "\n" . $this->layout->section($lines);
    }
}
