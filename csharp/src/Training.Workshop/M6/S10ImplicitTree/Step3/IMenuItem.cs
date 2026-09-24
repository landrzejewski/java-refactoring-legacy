using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S10ImplicitTree.Step3;

/// <summary>Krok 3: jawny Composite - produkt (liść) albo zestaw (węzeł).</summary>
public interface IMenuItem
{
    string Name { get; }

    Money Price { get; }

    void Render(int depth, StringBuilder text);
}
