namespace Training.Workshop.M6.S11SafeComposite.Step1;

/// <summary>Krok 1: tam, gdzie klient dodaje dzieci, zmienna ma typ Combo - kompilator pilnuje reszty.</summary>
public sealed class ComboCatalog
{
    public MenuComponent Find(string code) => code switch
    {
        "family" => Family(),
        "duo" => Duo(),
        "nachos" => new Product("Nachos", "14.00"),
        _ => throw new ArgumentException("unknown combo: " + code),
    };

    private static MenuComponent Family()
    {
        var combo = new Combo("Zestaw Rodzinny");
        combo.Add(new Product("Popcorn XL", "24.00"));
        var drinks = new Combo("Napoje");
        drinks.Add(new Product("Cola", "9.00"));
        drinks.Add(new Product("Cola", "9.00"));
        drinks.Add(new Product("Woda", "7.00"));
        combo.Add(drinks);
        return combo;
    }

    private static MenuComponent Duo()
    {
        var combo = new Combo("Zestaw Duo");
        combo.Add(new Product("Popcorn L", "18.00"));
        combo.Add(new Product("Cola", "9.00"));
        combo.Add(new Product("Cola", "9.00"));
        return combo;
    }
}
