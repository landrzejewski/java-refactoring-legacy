<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

/** Krok 2: baner promocyjny - nie potrzebuje danych sprzedaży, tylko układu kanału. */
final readonly class PromoBanner implements Document
{
    public function __construct(public Document $inner, public ReceiptLayout $layout)
    {
    }

    public function render(): string
    {
        return $this->inner->render() . "\n" . $this->layout->section(['Kod KINO10: -10% na nastepny bilet online']);
    }
}
