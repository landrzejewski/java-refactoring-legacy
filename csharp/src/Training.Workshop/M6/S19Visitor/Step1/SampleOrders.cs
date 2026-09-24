using Training.Workshop.Shared;

namespace Training.Workshop.M6.S19Visitor.Step1;

/// <summary>Krok 1: bez zmian - przykładowe zamówienia (bilety, bar, vouchery).</summary>
public sealed class SampleOrders
{
    public IReadOnlyList<IOrderItem> Find(string code) => code switch
    {
        "evening" =>
        [
            new TicketItem("Diuna", "IMAX", Money.Of("40.00")),
            new SnackItem("Popcorn L", Money.Of("18.00")),
            new SnackItem("Cola", Money.Of("9.00")),
            new VoucherItem("KINO20", Money.Of("20.00")),
        ],
        "voucher" =>
        [
            new TicketItem("Amator", "2D", Money.Of("25.00")),
            new VoucherItem("KINO20", Money.Of("20.00")),
        ],
        "empty" => [],
        _ => throw new ArgumentException("unknown order: " + code),
    };
}
