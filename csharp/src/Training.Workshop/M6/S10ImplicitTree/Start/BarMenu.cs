using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S10ImplicitTree.Start;

/// <summary>
/// Start: ukryte drzewo - zestaw combo to lista, której pierwszy element jest nazwą, a kolejne
/// to "nazwa=cena" albo zagnieżdżone listy. Każda operacja powtarza is i rzutowania,
/// a Render dla każdego węzła liczy cenę od nowa (koszt kwadratowy).
/// </summary>
public sealed class BarMenu
{
    public Money Price(IReadOnlyList<object> combo)
    {
        RequireName(combo);
        var total = Money.Zero;
        foreach (var element in combo.Skip(1))
        {
            if (element is string product)
            {
                total = total.Plus(ProductPrice(product));
            }
            else if (element is IReadOnlyList<object> nested)
            {
                total = total.Plus(Price(nested));
            }
            else
            {
                throw new ArgumentException("unsupported element: " + element);
            }
        }
        return total;
    }

    public string Render(IReadOnlyList<object> combo)
    {
        var text = new StringBuilder();
        Render(combo, 0, text);
        return text.ToString();
    }

    private void Render(IReadOnlyList<object> combo, int depth, StringBuilder text)
    {
        text.Append(Indent(depth)).Append(combo[0])
            .Append(' ').Append(Price(combo)).Append('\n');
        foreach (var element in combo.Skip(1))
        {
            if (element is string product)
            {
                text.Append(Indent(depth + 1)).Append(product, 0, product.IndexOf('='))
                    .Append(' ').Append(ProductPrice(product)).Append('\n');
            }
            else if (element is IReadOnlyList<object> nested)
            {
                Render(nested, depth + 1, text);
            }
            else
            {
                throw new ArgumentException("unsupported element: " + element);
            }
        }
    }

    private static void RequireName(IReadOnlyList<object> combo)
    {
        if (combo.Count == 0 || combo[0] is not string)
        {
            throw new ArgumentException("combo needs a name");
        }
    }

    private static Money ProductPrice(string product)
    {
        if (!product.Contains('='))
        {
            throw new ArgumentException("product needs a price: " + product);
        }
        return Money.Of(product[(product.IndexOf('=') + 1)..]);
    }

    private static string Indent(int depth) => new(' ', 2 * depth);
}
