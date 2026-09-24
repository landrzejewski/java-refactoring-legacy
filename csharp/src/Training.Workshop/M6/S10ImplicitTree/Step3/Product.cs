using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S10ImplicitTree.Step3;

/// <summary>Krok 3: liść - produkt baru z ceną.</summary>
public sealed record Product(string Name, Money Price) : IMenuItem
{
    public void Render(int depth, StringBuilder text)
    {
        text.Append(new string(' ', 2 * depth)).Append(Name).Append(' ').Append(Price).Append('\n');
    }
}
