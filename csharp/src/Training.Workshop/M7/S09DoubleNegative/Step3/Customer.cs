namespace Training.Workshop.M7.S09DoubleNegative.Step3;

/// <summary>
/// Krok 3: odwrócenie delegacji - właściwość nazywa się Vip, negatyw zniknął.
/// Uwaga: zmienia się znaczenie argumentu konstruktora pozycyjnego (true = VIP) - wywołania
/// pozycyjne kompilują się dalej, tylko te z nazwanym argumentem (NotVip: ...) przestaną.
/// Gdyby rekord trafiał do JSON-a lub bazy, zmieniłaby się też granica - to wymaga migracji.
/// </summary>
public sealed record Customer(string Email, bool Vip);
