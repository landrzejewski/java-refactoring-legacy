<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\PrintOptions;
use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Krok 2: składa dokument z rdzenia i opcjonalnych sekcji. Kolejność owijania jest kontraktem
 * (VAT, promocja, stopka lojalnościowa) i jest zapisana tylko tutaj.
 */
final readonly class DocumentComposer
{
    public function __construct(private DocumentFactory $family)
    {
    }

    public function compose(Sale $sale, PrintOptions $options): Document
    {
        $layout = $this->family->receiptLayout();
        $document = new ChannelDocument($sale, $this->family);
        if ($options->withVat) {
            $document = new VatSection($document, $layout, $sale);
        }
        if ($options->withPromo) {
            $document = new PromoBanner($document, $layout);
        }
        if ($options->withLoyaltyFooter) {
            $document = new LoyaltyFooter($document, $layout, $sale);
        }
        return $document;
    }
}
