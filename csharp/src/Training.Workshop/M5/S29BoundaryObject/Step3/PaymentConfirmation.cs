using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step3;

/// <summary>Wynik autoryzacji w języku kina (w Javie typ zagnieżdżony PaymentConfirmation.Outcome).</summary>
public enum Outcome { Approved, Declined, Error }

/// <summary>
/// Krok 3: bez zmian - Boundary Object - odpowiedź terminala jako niezmienna wartość w języku kina.
/// Walidacja w konstruktorze: obiekt, który istnieje, jest poprawny, więc dalszy kod nie musi
/// niczego sprawdzać ani zgadywać. Kwota jako Money, wynik jako enum, kod nigdy pusty.
/// Właściwości tylko do odczytu (bez init), więc wyrażenie <c>with</c> nie ominie walidacji.
/// </summary>
public sealed record PaymentConfirmation
{
    public PaymentConfirmation(Outcome outcome, string code, Money authorized)
    {
        ArgumentNullException.ThrowIfNull(authorized);
        if (string.IsNullOrWhiteSpace(code))
        {
            throw new ArgumentException("code must not be blank", nameof(code));
        }
        if (authorized.CompareTo(Money.Zero) < 0)
        {
            throw new ArgumentException("authorized amount must not be negative", nameof(authorized));
        }
        if (outcome != Outcome.Approved && authorized.IsGreaterThan(Money.Zero))
        {
            throw new ArgumentException("only approved payment can authorize money", nameof(authorized));
        }
        Outcome = outcome;
        Code = code;
        Authorized = authorized;
    }

    public Outcome Outcome { get; }

    public string Code { get; }

    public Money Authorized { get; }

    public bool Approved => Outcome == Outcome.Approved;

    /// <summary>Autoryzacja częściowa: terminal zgodził się na mniej, niż prosiliśmy.</summary>
    public bool IsPartialFor(Money requested)
    {
        return Approved && Authorized.CompareTo(requested) < 0;
    }
}
