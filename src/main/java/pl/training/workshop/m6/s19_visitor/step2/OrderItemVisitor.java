package pl.training.workshop.m6.s19_visitor.step2;

/** Krok 2: klasyczny Visitor - jedna metoda na rodzaj pozycji; brak metody = błąd kompilacji. */
public interface OrderItemVisitor<R> {
    R visitTicket(TicketItem ticket);

    R visitSnack(SnackItem snack);

    R visitVoucher(VoucherItem voucher);
}
