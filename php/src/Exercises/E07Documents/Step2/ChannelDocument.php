<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Sale;

/** Krok 2: rdzeń dokumentu - bilet, kod i paragon z jednej rodziny, bez żadnej opcji. */
final readonly class ChannelDocument implements Document
{
    public function __construct(public Sale $sale, public DocumentFactory $family)
    {
    }

    public function render(): string
    {
        return $this->family->ticketLayout()->ticket($this->sale) . "\n"
            . $this->family->codeRenderer()->code($this->sale) . "\n"
            . $this->family->receiptLayout()->receipt($this->sale);
    }
}
