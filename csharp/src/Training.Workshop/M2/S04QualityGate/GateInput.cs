namespace Training.Workshop.M2.S04QualityGate;

/// <summary>Stabilny kontrakt sceny: co bramka ma sprawdzić.</summary>
/// <param name="Sources">katalog ze źródłami domeny (bez podkatalogów)</param>
/// <param name="TestSource">plik testu kluczowej klasy</param>
/// <param name="KeyClass">prosta nazwa kluczowej klasy domeny, np. PriceTable</param>
/// <param name="TestClass">pełna nazwa klasy testowej do uruchomienia</param>
public sealed record GateInput(string Sources, string TestSource, string KeyClass, string TestClass);
