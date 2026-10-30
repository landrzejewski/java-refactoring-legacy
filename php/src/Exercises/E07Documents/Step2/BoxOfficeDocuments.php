<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

/** Krok 2: zestaw dokumentów kasy (A4). */
final class BoxOfficeDocuments implements DocumentFactory
{
    public function ticketLayout(): TicketLayout
    {
        return new BoxOfficeTicketLayout();
    }

    public function codeRenderer(): CodeRenderer
    {
        return new BoxOfficeCodeRenderer();
    }

    public function receiptLayout(): ReceiptLayout
    {
        return new BoxOfficeReceiptLayout();
    }
}
