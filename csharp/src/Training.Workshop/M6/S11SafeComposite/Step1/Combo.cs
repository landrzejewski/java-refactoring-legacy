using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Step1;

/// <summary>Krok 1: zarządzanie dziećmi tylko w węźle; Describe() rozszerza opis o dzieci.</summary>
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

    public void Add(MenuComponent child)
    {
        _children.Add(child);
    }

    public IReadOnlyList<MenuComponent> Children => _children.ToList().AsReadOnly();

    public override string Describe()
    {
        if (_children.Count == 0)
        {
            return base.Describe();
        }
        return base.Describe() + " [" + string.Join(", ", _children.Select(child => child.Describe())) + "]";
    }
}
