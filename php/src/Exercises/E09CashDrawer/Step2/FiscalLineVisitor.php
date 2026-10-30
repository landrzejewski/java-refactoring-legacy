<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

/**
 * Krok 2: operacja "linia eksportu fiskalnego" (rodzaj;numer;kwota;stawka VAT[;karta]).
 * Pogotowie kasowe nie jest fiskalne - pusty tekst, raport go pomija jak w start.
 *
 * @implements DrawerEntryVisitor<string>
 */
final class FiscalLineVisitor implements DrawerEntryVisitor
{
    public function visitCashSale(CashSale $sale): string
    {
        return 'S;' . $sale->reference . ';' . $sale->amount . ';8';
    }

    public function visitCardSale(CardSale $sale): string
    {
        return 'K;' . $sale->reference . ';' . $sale->amount . ';8;' . $sale->cardLast4;
    }

    public function visitRefund(Refund $refund): string
    {
        return 'Z;' . $refund->reference . ';-' . $refund->amount . ';8';
    }

    public function visitDeposit(Deposit $deposit): string
    {
        return '';
    }
}
