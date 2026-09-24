using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S10ImplicitTree.Step1;

/// <summary>Krok 1: węzeł - zestaw, którego cena to suma elementów. Niemutowalny.</summary>
public sealed record Combo(string Name, IReadOnlyList<IMenuItem> Items) : IMenuItem
{
    public IReadOnlyList<IMenuItem> Items { get; } = Items.ToList().AsReadOnly();

    public Money Price
    {
        get
        {
            var total = Money.Zero;
            foreach (var item in Items)
            {
                total = total.Plus(item.Price);
            }
            return total;
        }
    }

    public void Render(int depth, StringBuilder text)
    {
        text.Append(new string(' ', 2 * depth)).Append(Name).Append(' ').Append(Price).Append('\n');
        foreach (var item in Items)
        {
            item.Render(depth + 1, text);
        }
    }
}
