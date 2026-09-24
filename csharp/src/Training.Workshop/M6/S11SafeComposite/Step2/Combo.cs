using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Step2;

/// <summary>Krok 2: węzeł z dziećmi podanymi przy tworzeniu - Add() nie istnieje nigdzie.</summary>
public sealed record Combo : MenuComponent
{
    public Combo(string name, IEnumerable<MenuComponent> children)
    {
        Name = name;
        Children = children.ToList().AsReadOnly();
    }

    public override string Name { get; }

    public IReadOnlyList<MenuComponent> Children { get; }

    public static Combo Of(string name, params MenuComponent[] children)
    {
        return new Combo(name, children);
    }

    public override Money Price
    {
        get
        {
            var total = Money.Zero;
            foreach (var child in Children)
            {
                total = total.Plus(child.Price);
            }
            return total;
        }
    }

    public override string Describe()
    {
        var text = Name + " " + Price;
        if (Children.Count == 0)
        {
            return text;
        }
        return text + " [" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
