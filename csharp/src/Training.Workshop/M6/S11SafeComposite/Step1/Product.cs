using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Step1;

/// <summary>Krok 1: liść nie ma już metody Add() - nie da się jej wywołać przez pomyłkę.</summary>
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
