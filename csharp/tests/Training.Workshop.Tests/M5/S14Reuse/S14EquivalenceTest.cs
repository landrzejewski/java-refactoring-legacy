using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S14Reuse;

/// <summary>Test równoważności: naliczanie punktów i raport identyczne dla obu kont w start i każdym kroku.</summary>
public sealed class S14EquivalenceTest
{
    public sealed record History(bool Corporate, string Owner, IReadOnlyList<string> Payments);

    private static readonly Scene<History, string> Scene = Support.Scene.Variants<History, string>()
        .Variant("start", h =>
        {
            if (h.Corporate)
            {
                var corporate = new Training.Workshop.M5.S14Reuse.Start.CorporateAccount(h.Owner);
                foreach (var p in h.Payments)
                {
                    corporate.Earn(Money.Of(p));
                }
                return new Training.Workshop.M5.S14Reuse.Start.LoyaltyReport().Line(corporate);
            }
            var account = new Training.Workshop.M5.S14Reuse.Start.LoyaltyAccount(h.Owner);
            foreach (var p in h.Payments)
            {
                account.Earn(Money.Of(p));
            }
            return new Training.Workshop.M5.S14Reuse.Start.LoyaltyReport().Line(account);
        })
        .Variant("step1", h =>
        {
            if (h.Corporate)
            {
                var corporate = new Training.Workshop.M5.S14Reuse.Step1.CorporateAccount(h.Owner);
                foreach (var p in h.Payments)
                {
                    corporate.Earn(Money.Of(p));
                }
                return new Training.Workshop.M5.S14Reuse.Step1.LoyaltyReport().Line(corporate);
            }
            var account = new Training.Workshop.M5.S14Reuse.Step1.LoyaltyAccount(h.Owner);
            foreach (var p in h.Payments)
            {
                account.Earn(Money.Of(p));
            }
            return new Training.Workshop.M5.S14Reuse.Step1.LoyaltyReport().Line(account);
        })
        .Variant("step2", h =>
        {
            if (h.Corporate)
            {
                var corporate = new Training.Workshop.M5.S14Reuse.Step2.CorporateAccount(h.Owner);
                foreach (var p in h.Payments)
                {
                    corporate.Earn(Money.Of(p));
                }
                return new Training.Workshop.M5.S14Reuse.Step2.LoyaltyReport().Line(corporate);
            }
            var account = new Training.Workshop.M5.S14Reuse.Step2.LoyaltyAccount(h.Owner);
            foreach (var p in h.Payments)
            {
                account.Earn(Money.Of(p));
            }
            return new Training.Workshop.M5.S14Reuse.Step2.LoyaltyReport().Line(account);
        })
        .Expect("klient: pełne dziesiątki", new History(false, "anna@kino.pl", ["45.00", "32.00"]),
            "anna@kino.pl: 7 pkt")
        .Expect("firma: duża transakcja", new History(true, "Kino-Tech", ["999.99"]),
            "Kino-Tech: 99 pkt")
        .Expect("klient bez zakupów", new History(false, "jan@kino.pl", []), "jan@kino.pl: 0 pkt");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepReportsPointsTheSameWay(string test) => Scene.Run(test);
}
