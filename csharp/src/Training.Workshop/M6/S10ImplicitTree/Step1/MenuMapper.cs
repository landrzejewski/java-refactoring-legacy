using Training.Workshop.Shared;

namespace Training.Workshop.M6.S10ImplicitTree.Step1;

/// <summary>
/// Krok 1: mapper starego formatu (zagnieżdżone listy) na Composite. Zachowuje komunikaty
/// błędów starego kodu - to też jest obserwowalne zachowanie.
/// </summary>
public static class MenuMapper
{
    public static Combo FromNested(IReadOnlyList<object> combo)
    {
        if (combo.Count == 0 || combo[0] is not string name)
        {
            throw new ArgumentException("combo needs a name");
        }
        var items = new List<IMenuItem>();
        foreach (var element in combo.Skip(1))
        {
            items.Add(element switch
            {
                string product => Product(product),
                IReadOnlyList<object> nested => FromNested(nested),
                _ => throw new ArgumentException("unsupported element: " + element),
            });
        }
        return new Combo(name, items);
    }

    private static Product Product(string text)
    {
        if (!text.Contains('='))
        {
            throw new ArgumentException("product needs a price: " + text);
        }
        var separator = text.IndexOf('=');
        return new Product(text[..separator], Money.Of(text[(separator + 1)..]));
    }
}
