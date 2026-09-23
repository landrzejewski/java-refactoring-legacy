package pl.training.workshop.m6.s19_visitor.step1;

/** Krok 1: operacja "linia paragonu" jako Visitor. */
public final class ReceiptLineVisitor implements OrderItemVisitor<String> {
    @Override
    public String visitTicket(TicketItem ticket) {
        return "Bilet " + ticket.title() + " " + ticket.format() + " " + ticket.price();
    }

    @Override
    public String visitSnack(SnackItem snack) {
        return snack.name() + " " + snack.price();
    }

    @Override
    public String visitVoucher(VoucherItem voucher) {
        return "Voucher " + voucher.code() + " -" + voucher.value();
    }
}
