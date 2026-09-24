namespace Training.Workshop.M6.S18CollectingParameter.Step3;

/// <summary>
/// Krok 3: wąski parametr zbierający - można tylko dopisać ostrzeżenie. Metody pomocnicze
/// nie mogą niczego usunąć, wyczyścić ani przestawić.
/// </summary>
public sealed class Warnings
{
    private readonly List<string> _items = [];

    public void Add(string warning)
    {
        _items.Add(warning);
    }

    public string Summary()
    {
        return _items.Count == 0 ? "OK" : string.Join("; ", _items);
    }
}
