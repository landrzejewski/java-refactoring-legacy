namespace Training.Workshop.M6.S19Visitor.Step2;

/// <summary>Krok 2: klasyczny Visitor - jedna metoda na rodzaj pozycji; brak metody = błąd kompilacji.</summary>
public interface IOrderItemVisitor<out TResult>
{
    TResult VisitTicket(TicketItem ticket);

    TResult VisitSnack(SnackItem snack);

    TResult VisitVoucher(VoucherItem voucher);
}
