using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S06ExtractInterface;

/// <summary>Test równoważności: podsumowanie koszyka (suma i VAT 8%/23%) identyczne w start i każdym kroku.</summary>
public sealed class S06EquivalenceTest
{
    public sealed record Basket(IReadOnlyList<string> Tickets, IReadOnlyList<string> Snacks);

    private static readonly Scene<Basket, string> Scene = Support.Scene.Variants<Basket, string>()
        .Variant("start", b =>
        {
            var cart = new Training.Workshop.M5.S06ExtractInterface.Start.Cart();
            foreach (var p in b.Tickets)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Start.Ticket("Diuna", "H7", Money.Of(p)));
            }
            foreach (var p in b.Snacks)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Start.Snack("Popcorn", Money.Of(p)));
            }
            return cart.Summary();
        })
        .Variant("step1", b =>
        {
            var cart = new Training.Workshop.M5.S06ExtractInterface.Step1.Cart();
            foreach (var p in b.Tickets)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Step1.Ticket("Diuna", "H7", Money.Of(p)));
            }
            foreach (var p in b.Snacks)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Step1.Snack("Popcorn", Money.Of(p)));
            }
            return cart.Summary();
        })
        .Variant("step2", b =>
        {
            var cart = new Training.Workshop.M5.S06ExtractInterface.Step2.Cart();
            foreach (var p in b.Tickets)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Step2.Ticket("Diuna", "H7", Money.Of(p)));
            }
            foreach (var p in b.Snacks)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Step2.Snack("Popcorn", Money.Of(p)));
            }
            return cart.Summary();
        })
        .Variant("step3", b =>
        {
            var cart = new Training.Workshop.M5.S06ExtractInterface.Step3.Cart();
            foreach (var p in b.Tickets)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Step3.Ticket("Diuna", "H7", Money.Of(p)));
            }
            foreach (var p in b.Snacks)
            {
                cart.Add(new Training.Workshop.M5.S06ExtractInterface.Step3.Snack("Popcorn", Money.Of(p)));
            }
            return cart.Summary();
        })
        .Expect("dwa bilety 2D i popcorn", new Basket(["25.00", "25.00"], ["18.00"]),
            "Razem: 68.00, VAT: 7.07")
        .Expect("bilet IMAX i napój", new Basket(["40.00"], ["9.00"]),
            "Razem: 49.00, VAT: 4.64")
        .Expect("sam bar", new Basket([], ["9.00"]), "Razem: 9.00, VAT: 1.68")
        .Expect("pusty koszyk", new Basket([], []), "Razem: 0.00, VAT: 0.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSummarizesTheCartTheSameWay(string test) => Scene.Run(test);
}
