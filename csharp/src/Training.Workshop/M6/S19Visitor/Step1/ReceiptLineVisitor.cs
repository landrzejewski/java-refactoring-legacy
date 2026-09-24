namespace Training.Workshop.M6.S19Visitor.Step1;

/// <summary>Krok 1: operacja "linia paragonu" jako Visitor.</summary>
public sealed class ReceiptLineVisitor : IOrderItemVisitor<string>
{
    public string VisitTicket(TicketItem ticket)
    {
        return "Bilet " + ticket.Title + " " + ticket.Format + " " + ticket.Price;
    }

    public string VisitSnack(SnackItem snack)
    {
        return snack.Name + " " + snack.Price;
    }

    public string VisitVoucher(VoucherItem voucher)
    {
        return "Voucher " + voucher.Code + " -" + voucher.Value;
    }
}
