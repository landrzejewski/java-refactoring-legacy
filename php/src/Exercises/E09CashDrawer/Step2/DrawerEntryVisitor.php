<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E09CashDrawer\Step2;

/**
 * Krok 2: jedna operacja na wpisach szuflady = jedna implementacja. Jedna metoda na rodzaj wpisu,
 * więc brak obsługi nowego rodzaju jest błędem już przy ładowaniu klasy (niezaimplementowana metoda
 * interfejsu), a nie wyjątkiem przy zamknięciu dnia.
 *
 * @template R
 */
interface DrawerEntryVisitor
{
    /** @return R */
    public function visitCashSale(CashSale $sale): mixed;

    /** @return R */
    public function visitCardSale(CardSale $sale): mixed;

    /** @return R */
    public function visitRefund(Refund $refund): mixed;

    /** @return R */
    public function visitDeposit(Deposit $deposit): mixed;
}
