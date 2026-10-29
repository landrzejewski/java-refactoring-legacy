package pl.training.workshop.exercises.e09_cashdrawer.step1;

/**
 * Krok 1: jedna operacja na wpisach szuflady = jedna implementacja. Jedna metoda na rodzaj wpisu,
 * więc brak obsługi nowego rodzaju jest błędem kompilacji, a nie wyjątkiem przy zamknięciu dnia.
 */
public interface DrawerEntryVisitor<R> {
    R visitCashSale(CashSale sale);

    R visitCardSale(CardSale sale);

    R visitRefund(Refund refund);

    R visitDeposit(Deposit deposit);
}
