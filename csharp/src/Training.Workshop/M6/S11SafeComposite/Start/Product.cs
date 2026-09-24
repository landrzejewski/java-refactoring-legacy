using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Start;

/// <summary>Start: liść dziedziczy Add(), który zawsze rzuca wyjątek.</summary>
public sealed class Product : MenuComponent
{
    private readonly string _name;
    private readonly Money _price;

    public Product(string name, string price)
    {
        _name = name;
        _price = Money.Of(price);
    }

    public override string Name => _name;

    public override Money Price => _price;
}
