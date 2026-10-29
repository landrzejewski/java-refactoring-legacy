package pl.training.workshop.exercises.e09_cashdrawer.step2;

/**
 * Krok 2: operacja "linia eksportu fiskalnego" (rodzaj;numer;kwota;stawka VAT[;karta]).
 * Pogotowie kasowe nie jest fiskalne - pusty tekst, raport go pomija jak w start.
 */
public final class FiscalLineVisitor implements DrawerEntryVisitor<String> {
    @Override
    public String visitCashSale(CashSale sale) {
        return "S;" + sale.reference() + ";" + sale.amount() + ";8";
    }

    @Override
    public String visitCardSale(CardSale sale) {
        return "K;" + sale.reference() + ";" + sale.amount() + ";8;" + sale.cardLast4();
    }

    @Override
    public String visitRefund(Refund refund) {
        return "Z;" + refund.reference() + ";-" + refund.amount() + ";8";
    }

    @Override
    public String visitDeposit(Deposit deposit) {
        return "";
    }
}
