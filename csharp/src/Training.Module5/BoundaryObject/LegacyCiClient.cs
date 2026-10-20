namespace Training.Module5.BoundaryObject;

/// <summary>
/// Zewnętrzny system CI: zwraca surowy, nietypowany ładunek
/// (klucze <c>id, status, coverage, failed, commit</c>, wszystkie wartości jako tekst).
/// </summary>
public delegate IReadOnlyDictionary<string, string> LegacyCiClient(string buildId);
