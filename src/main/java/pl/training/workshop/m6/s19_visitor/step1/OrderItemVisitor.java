package pl.training.workshop.m6.s19_visitor.step1;

/** Krok 1: klasyczny Visitor - jedna metoda na rodzaj pozycji; brak metody = błąd kompilacji. */
public interface OrderItemVisitor<R> {
    R visitTicket(TicketItem ticket);

    R visitSnack(SnackItem snack);

    R visitVoucher(VoucherItem voucher);
}
