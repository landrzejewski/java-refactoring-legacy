using System.Text;
using Training.Workshop.M6.S10ImplicitTree.Step3;

namespace Training.Workshop.Tests.M6.S10ImplicitTree;

/// <summary>
/// Test różnicowy: stara reprezentacja (Start) kontra nowa na 500 losowych drzewach
/// (stałe ziarno - wynik powtarzalny). Uzupełnia, a nie zastępuje niezależne oczekiwania.
/// </summary>
public sealed class S10DifferentialTest
{
    private readonly Func<IReadOnlyList<object>, string> _legacy = S10EquivalenceTest.Safe(
        new Training.Workshop.M6.S10ImplicitTree.Start.BarMenu().Price,
        new Training.Workshop.M6.S10ImplicitTree.Start.BarMenu().Render);

    [Fact]
    public void CompositeFromMapperMatchesLegacyAlready()
    {
        var composite = S10EquivalenceTest.Safe(
            definition => MenuMapper.FromNested(definition).Price,
            definition =>
            {
                var text = new StringBuilder();
                MenuMapper.FromNested(definition).Render(0, text);
                return text.ToString();
            });
        Compare(composite);
    }

    [Fact]
    public void FinalBarMenuMatchesLegacy()
    {
        Compare(S10EquivalenceTest.Safe(
            new BarMenu().Price,
            new BarMenu().Render));
    }

    private void Compare(Func<IReadOnlyList<object>, string> candidate)
    {
        var random = new Random(42);
        for (var i = 0; i < 500; i++)
        {
            var definition = RandomCombo(random, 0);
            var expected = _legacy(definition);
            var actual = candidate(definition);
            Assert.True(expected == actual,
                "drzewo: " + Show(definition) + "\noczekiwano:\n" + expected + "\notrzymano:\n" + actual);
        }
    }

    private static List<object> RandomCombo(Random random, int depth)
    {
        var combo = new List<object> { "Zestaw " + random.Next(100) };
        var size = random.Next(4);
        for (var i = 0; i < size; i++)
        {
            var kind = random.Next(20);
            if (kind == 0)
            {
                combo.Add(7);
            }
            else if (kind == 1)
            {
                combo.Add("Bez ceny");
            }
            else if (kind < 6 && depth < 3)
            {
                combo.Add(RandomCombo(random, depth + 1));
            }
            else
            {
                combo.Add("P" + random.Next(50) + "=" + random.Next(30) + "." + random.Next(10) + "0");
            }
        }
        return combo;
    }

    /// <summary>Wydruk drzewa jak List.toString() w Javie, np. [Zestaw 1, P3=4.50, [Zestaw 2]].</summary>
    private static string Show(object element)
    {
        return element is IReadOnlyList<object> list
            ? "[" + string.Join(", ", list.Select(Show)) + "]"
            : element.ToString() ?? "null";
    }
}
