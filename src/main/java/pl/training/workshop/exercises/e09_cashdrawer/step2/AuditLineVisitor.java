package pl.training.workshop.exercises.e09_cashdrawer.step2;

/** Krok 2: operacja "linia audytu" - czytelny opis każdego wpisu. */
public final class AuditLineVisitor implements DrawerEntryVisitor<String> {
    @Override
    public String visitCashSale(CashSale sale) {
        return "CASH " + sale.reference() + " " + sale.amount();
    }

    @Override
    public String visitCardSale(CardSale sale) {
        return "CARD " + sale.reference() + " " + sale.amount() + " karta ****" + sale.cardLast4();
    }

    @Override
    public String visitRefund(Refund refund) {
        return "REFUND " + refund.reference() + " -" + refund.amount() + " powod: " + refund.reason();
    }

    @Override
    public String visitDeposit(Deposit deposit) {
        return "DEPOSIT " + deposit.amount() + " " + deposit.note();
    }
}
