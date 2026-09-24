using System.Text;
using Training.Workshop.Shared;

namespace Training.Workshop.M6.S10ImplicitTree.Step3;

/// <summary>
/// Krok 3: Render też na Composite. BarMenu tylko mapuje stary format na drzewo - format
/// trwały (zagnieżdżone listy) zmienimy osobnym krokiem, jeśli w ogóle.
/// </summary>
public sealed class BarMenu
{
    public Money Price(IReadOnlyList<object> combo)
    {
        return MenuMapper.FromNested(combo).Price;
    }

    public string Render(IReadOnlyList<object> combo)
    {
        var text = new StringBuilder();
        MenuMapper.FromNested(combo).Render(0, text);
        return text.ToString();
    }
}
