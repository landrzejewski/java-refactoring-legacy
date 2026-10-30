<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

/** Krok 1: zestaw dokumentów aplikacji mobilnej. */
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
