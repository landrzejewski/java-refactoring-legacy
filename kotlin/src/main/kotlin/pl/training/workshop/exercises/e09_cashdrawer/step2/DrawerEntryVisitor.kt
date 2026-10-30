package pl.training.workshop.exercises.e09_cashdrawer.step2

/**
 * Krok 2: jedna operacja na wpisach szuflady = jedna implementacja. Jedna metoda na rodzaj wpisu,
 * więc brak obsługi nowego rodzaju jest błędem kompilacji, a nie wyjątkiem przy zamknięciu dnia.
 */
interface DrawerEntryVisitor<R> {
    fun visitCashSale(sale: CashSale): R

    fun visitCardSale(sale: CardSale): R

    fun visitRefund(refund: Refund): R

    fun visitDeposit(deposit: Deposit): R
}
