<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Start;

use Training\Workshop\Exercises\E07Documents\Sale;

/**
 * Start: skleja trzy części dokumentu. Przyjmuje je osobno, więc nic nie broni
 * złożyć biletu kioskowego z kodem QR aplikacji i paragonem A4 - kontrola typów tego nie zauważy.
 */
final readonly class DocumentComposer
{
    public function __construct(
        private TicketLayout $ticketLayout,
        private CodeRenderer $codeRenderer,
        private ReceiptLayout $receiptLayout,
    ) {
    }

    public function compose(Sale $sale): string
    {
        return $this->ticketLayout->ticket($sale) . "\n" . $this->codeRenderer->code($sale) . "\n" . $this->receiptLayout->receipt($sale);
    }
}
