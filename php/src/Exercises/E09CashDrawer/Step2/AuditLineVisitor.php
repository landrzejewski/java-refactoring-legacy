<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

/**
 * Krok 2: operacja "linia audytu" - czytelny opis każdego wpisu.
 *
 * @implements DrawerEntryVisitor<string>
 */
final class AuditLineVisitor implements DrawerEntryVisitor
{
    public function visitCashSale(CashSale $sale): string
    {
        return 'CASH ' . $sale->reference . ' ' . $sale->amount;
    }

    public function visitCardSale(CardSale $sale): string
    {
        return 'CARD ' . $sale->reference . ' ' . $sale->amount . ' karta ****' . $sale->cardLast4;
    }

    public function visitRefund(Refund $refund): string
    {
        return 'REFUND ' . $refund->reference . ' -' . $refund->amount . ' powod: ' . $refund->reason;
    }

    public function visitDeposit(Deposit $deposit): string
    {
        return 'DEPOSIT ' . $deposit->amount . ' ' . $deposit->note;
    }
}
