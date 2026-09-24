namespace Training.Workshop.Tests.Support;

/// <summary>
/// Pomocnik testów równoważności scen warsztatu.
/// Każdy wariant sceny (Start, Step1, ...) jest adaptowany do wspólnej
/// funkcji <c>I -> O</c>, a następnie sprawdzany na tych samych przypadkach.
/// Każda para "wariant: przypadek" to osobny przypadek testu <c>[Theory]</c>.
/// </summary>
/// <example>
/// <code>
/// private static readonly Scene&lt;Order, string&gt; Scene = Scene.Variants&lt;Order, string&gt;()
///     .Variant("start", o => new Start.PriceCalculator().Price(o))
///     .Variant("step1", o => new Step1.PriceCalculator().Price(o))
///     .Expect("2D normal", order2d, "25.00");
///
/// public static TheoryData&lt;string&gt; Cases => Scene.Tests();
///
/// [Theory]
/// [MemberData(nameof(Cases))]
/// public void EveryVariantBehavesTheSame(string test) => Scene.Run(test);
/// </code>
/// </example>
public static class Scene
{
    public static Scene<TIn, TOut> Variants<TIn, TOut>() => new();
}

public sealed class Scene<TIn, TOut>
{
    private readonly List<(string Name, Func<TIn, TOut> Implementation)> _variants = [];
    private readonly List<(string Name, TIn Input, TOut Expected)> _cases = [];

    internal Scene()
    {
    }

    public Scene<TIn, TOut> Variant(string name, Func<TIn, TOut> implementation)
    {
        _variants.Add((name, implementation));
        return this;
    }

    public Scene<TIn, TOut> Expect(string name, TIn input, TOut expected)
    {
        _cases.Add((name, input, expected));
        return this;
    }

    /// <summary>Nazwy przypadków "wariant: przypadek" dla <c>[MemberData]</c>.</summary>
    public TheoryData<string> Tests()
    {
        var data = new TheoryData<string>();
        foreach (var variant in _variants)
        {
            foreach (var testCase in _cases)
            {
                data.Add(variant.Name + ": " + testCase.Name);
            }
        }
        return data;
    }

    /// <summary>Uruchamia jeden przypadek wskazany nazwą z <see cref="Tests"/>.</summary>
    public void Run(string test)
    {
        foreach (var variant in _variants)
        {
            foreach (var testCase in _cases)
            {
                if (variant.Name + ": " + testCase.Name == test)
                {
                    Assert.Equal(testCase.Expected, variant.Implementation(testCase.Input));
                    return;
                }
            }
        }
        throw new ArgumentException("nie ma przypadku " + test);
    }
}
