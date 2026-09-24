using Training.Workshop.Shared;

namespace Training.Workshop.M6.S11SafeComposite.Start;

/// <summary>
/// Start: Transparent Composite - Add() i Children we wspólnym typie. Klient może wywołać
/// Add() na liściu; kompilator milczy, błąd wychodzi dopiero w runtime.
/// </summary>
public abstract class MenuComponent
{
    public abstract string Name { get; }

    public abstract Money Price { get; }

    public virtual void Add(MenuComponent child)
    {
        throw new NotSupportedException("cannot add to " + Name);
    }

    public virtual IReadOnlyList<MenuComponent> Children => [];

    public string Describe()
    {
        var text = Name + " " + Price;
        if (Children.Count == 0)
        {
            return text;
        }
        return text + " [" + string.Join(", ", Children.Select(child => child.Describe())) + "]";
    }
}
