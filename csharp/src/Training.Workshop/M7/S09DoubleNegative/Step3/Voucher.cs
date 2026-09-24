namespace Training.Workshop.M7.S09DoubleNegative.Step3;

/// <summary>Krok 3: IsExpired() jest teraz jedyną definicją reguły; IsNotExpired() usunięte (Safe Delete).</summary>
public sealed record Voucher(string Code, DateOnly ValidUntil)
{
    public bool IsExpired(DateOnly today)
    {
        return today > ValidUntil;
    }
}
