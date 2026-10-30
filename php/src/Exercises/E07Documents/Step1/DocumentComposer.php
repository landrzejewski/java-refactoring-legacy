<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 1: skleja trzy części dokumentu pobrane z jednej rodziny. Nie da się już podać
 * biletu kioskowego z kodem QR - konstruktor przyjmuje zestaw, a nie trzy luźne części.
 */
final readonly class DocumentComposer
{
    public function __construct(private DocumentFactory $family)
    {
    }

    public function compose(Sale $sale): string
    {
        return $this->family->ticketLayout()->ticket($sale) . "\n"
            . $this->family->codeRenderer()->code($sale) . "\n"
            . $this->family->receiptLayout()->receipt($sale);
    }
}
