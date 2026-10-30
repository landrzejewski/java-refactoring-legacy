package pl.training.workshop.exercises.e09_cashdrawer.step2

/**
 * Krok 2: operacja "linia eksportu fiskalnego" (rodzaj;numer;kwota;stawka VAT[;karta]).
 * Pogotowie kasowe nie jest fiskalne - pusty tekst, raport go pomija jak w start.
 */
class FiscalLineVisitor : DrawerEntryVisitor<String> {
    override fun visitCashSale(sale: CashSale): String = "S;${sale.reference};${sale.amount};8"

    override fun visitCardSale(sale: CardSale): String = "K;${sale.reference};${sale.amount};8;${sale.cardLast4}"

    override fun visitRefund(refund: Refund): String = "Z;${refund.reference};-${refund.amount};8"

    override fun visitDeposit(deposit: Deposit): String = ""
}
