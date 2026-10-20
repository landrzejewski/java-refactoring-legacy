using Training.Workshop.Shared;

namespace Training.Workshop.M5.S32Mediator.Start;

/// <summary>
/// Start: podsumowanie ceny - rząd C (VIP) 35.00, pozostałe 25.00, voucher KINO10 to -10%.
/// Po wpisaniu vouchera samo pyta koszyk o miejsca i samo odświeża przycisk.
/// </summary>
public sealed class PriceSummary
{
    private int _discountPercent;
    private Money _total = Money.Zero;
    private Basket _basket = null!;
    private PayButton _payButton = null!;

    internal void Connect(Basket basket, PayButton payButton)
    {
        _basket = basket;
        _payButton = payButton;
    }

    /// <summary>Akcja użytkownika: voucher; nieznany kod usuwa zniżkę.</summary>
    public void ApplyVoucher(string code)
    {
        _discountPercent = code == "KINO10" ? 10 : 0;
        Recalculate(_basket.Seats);
        _payButton.Refresh(_basket.Seats.Count, _total);
    }

    internal void Recalculate(IReadOnlyList<string> seats)
    {
        var sum = Money.Zero;
        foreach (var seat in seats)
        {
            sum = sum.Plus(seat.StartsWith('C') ? Money.Of("35.00") : Money.Of("25.00"));
        }
        _total = sum.Minus(sum.Percent(_discountPercent));
    }

    internal Money Total => _total;

    internal string Describe()
    {
        return "suma=" + _total;
    }
}
