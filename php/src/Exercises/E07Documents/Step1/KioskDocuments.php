<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

/** Krok 1: zestaw dokumentów kiosku - jedyne miejsce, które zna klasy kioskowe. */
final class KioskDocuments implements DocumentFactory
{
    public function ticketLayout(): TicketLayout
    {
        return new KioskTicketLayout();
    }

    public function codeRenderer(): CodeRenderer
    {
        return new KioskCodeRenderer();
    }

    public function receiptLayout(): ReceiptLayout
    {
        return new KioskReceiptLayout();
    }
}
