using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Step2;

/// <summary>Krok 2: liść jako rekord.</summary>
public sealed record Product(string Name, Money Price) : MenuComponent
{
    public override string Name { get; } = Name;

    public override Money Price { get; } = Price;

    public Product(string name, string price)
        : this(name, Money.Of(price))
    {
    }

    public override string Describe()
    {
        return Name + " " + Price;
    }
}
