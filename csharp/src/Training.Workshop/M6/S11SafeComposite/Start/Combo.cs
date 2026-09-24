using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Start;

/// <summary>Start: zestaw nadpisuje Add() i Children.</summary>
public sealed class Combo : MenuComponent
{
    private readonly string _name;
    private readonly List<MenuComponent> _children = [];

    public Combo(string name)
    {
        _name = name;
    }

    public override string Name => _name;

    public override Money Price
    {
        get
        {
            var total = Money.Zero;
            foreach (var child in _children)
            {
                total = total.Plus(child.Price);
            }
            return total;
        }
    }

    public override void Add(MenuComponent child)
    {
        _children.Add(child);
    }

    public override IReadOnlyList<MenuComponent> Children => _children.ToList().AsReadOnly();
}
