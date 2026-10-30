<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

use Training\Workshop\Shared\Money;

/**
 * Krok 2: operacja "wpływ wpisu na gotówkę w szufladzie" wyjęta z łańcucha instanceof.
 *
 * @implements DrawerEntryVisitor<Money>
 */
final class CashEffectVisitor implements DrawerEntryVisitor
{
    public function visitCashSale(CashSale $sale): Money
    {
        return $sale->amount;
    }

    public function visitCardSale(CardSale $sale): Money
    {
        return Money::zero();
    }

    public function visitRefund(Refund $refund): Money
    {
        return Money::zero()->minus($refund->amount);
    }

    public function visitDeposit(Deposit $deposit): Money
    {
        return $deposit->amount;
    }
}
