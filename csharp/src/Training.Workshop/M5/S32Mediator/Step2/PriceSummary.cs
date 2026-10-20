using Training.Workshop.Shared;

namespace Training.Workshop.M5.S32Mediator.Step2;

/// <summary>
/// Krok 2: podsumowanie zna tylko mediatora. Liczy cenę z miejsc, które poda mu mediator
/// (rząd C - VIP 35.00, pozostałe 25.00, voucher KINO10 to -10%).
/// </summary>
public sealed class PriceSummary
{
    private int _discountPercent;
    private Money _total = Money.Zero;
    private readonly ISelectionMediator _mediator;

    public PriceSummary(ISelectionMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    /// <summary>Akcja użytkownika: voucher; nieznany kod usuwa zniżkę.</summary>
    public void ApplyVoucher(string code)
    {
        _discountPercent = code == "KINO10" ? 10 : 0;
        _mediator.VoucherApplied();
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
