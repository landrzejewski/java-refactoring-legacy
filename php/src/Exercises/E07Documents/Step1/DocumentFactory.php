<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step1;

/**
 * Krok 1: rodzina dokumentów jednego kanału. Klient dostaje cały spójny zestaw
 * (bilet, kod, paragon) z jednego miejsca i nie może pomieszać elementów różnych kanałów.
 */
interface DocumentFactory
{
    public function ticketLayout(): TicketLayout;

    public function codeRenderer(): CodeRenderer;

    public function receiptLayout(): ReceiptLayout;
}
