using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S10ImplicitTree;

/// <summary>Niezależne oczekiwania (policzone ręcznie) dla ceny i wydruku zestawów.</summary>
public sealed class S10EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<object>, string> Scene = Support.Scene.Variants<IReadOnlyList<object>, string>()
        .Variant("start", Safe(new Training.Workshop.M6.S10ImplicitTree.Start.BarMenu().Price,
            new Training.Workshop.M6.S10ImplicitTree.Start.BarMenu().Render))
        .Variant("step1", Safe(new Training.Workshop.M6.S10ImplicitTree.Step1.BarMenu().Price,
            new Training.Workshop.M6.S10ImplicitTree.Step1.BarMenu().Render))
        .Variant("step2", Safe(new Training.Workshop.M6.S10ImplicitTree.Step2.BarMenu().Price,
            new Training.Workshop.M6.S10ImplicitTree.Step2.BarMenu().Render))
        .Variant("step3", Safe(new Training.Workshop.M6.S10ImplicitTree.Step3.BarMenu().Price,
            new Training.Workshop.M6.S10ImplicitTree.Step3.BarMenu().Render))
        .Expect("zestaw z podzestawem", ["Zestaw Duo", "Popcorn L=18.00",
            new object[] { "Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00" }, "Nachos=14.00"], """
            50.00
            Zestaw Duo 50.00
              Popcorn L 18.00
              Napoje 18.00
                Cola 0.5 9.00
                Cola 0.5 9.00
              Nachos 14.00

            """)
        .Expect("pusty zestaw", ["Pusty"], "0.00\nPusty 0.00\n")
        .Expect("brak nazwy", [], "ERROR combo needs a name")
        .Expect("element nieobsługiwany", ["Zestaw", "Cola=9.00", 5], "ERROR unsupported element: 5")
        .Expect("produkt bez ceny", ["Zestaw", "Cola"], "ERROR product needs a price: Cola");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPricesAndRendersCombosTheSame(string test) => Scene.Run(test);

    internal static Func<IReadOnlyList<object>, string> Safe(
        Func<IReadOnlyList<object>, object> price, Func<IReadOnlyList<object>, string> render)
    {
        return definition =>
        {
            try
            {
                return price(definition) + "\n" + render(definition);
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }
}
