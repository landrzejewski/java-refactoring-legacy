<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

/** Krok 2: zestaw dokumentów aplikacji mobilnej. */
final class MobileDocuments implements DocumentFactory
{
    public function ticketLayout(): TicketLayout
    {
        return new MobileTicketLayout();
    }

    public function codeRenderer(): CodeRenderer
    {
        return new MobileCodeRenderer();
    }

    public function receiptLayout(): ReceiptLayout
    {
        return new MobileReceiptLayout();
    }
}
