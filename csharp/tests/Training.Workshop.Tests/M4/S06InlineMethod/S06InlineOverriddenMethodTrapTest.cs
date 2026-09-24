using Training.Workshop.M4.S06InlineMethod;

namespace Training.Workshop.Tests.M4.S06InlineMethod;

/// <summary>
/// Dokumentuje pułapkę: Inline Method na <c>BookingFee()</c> w klasie bazowej.
/// Podklasa nadal się kompiluje, <c>override</c> nadal jest poprawne - ale nikt już jej metody nie woła.
/// </summary>
public sealed class S06InlineOverriddenMethodTrapTest
{
    [Fact]
    public void InliningAnOverriddenMethodSilentlyDropsTheOnlineFee()
    {
        Assert.Equal("42.00", S06EquivalenceTest.Plain(new Training.Workshop.M4.S06InlineMethod.Step2
            .OnlineTicketPricing().Total(S06EquivalenceTest.ImaxEvening)));
        // po wklejeniu ciała BookingFee() z bazy internet sprzedaje bez opłaty
        Assert.Equal("40.00", S06EquivalenceTest.Plain(new NaiveOnlinePricing().Total(S06EquivalenceTest.ImaxEvening)));
    }

    /// <summary>Step2 po naiwnym Inline Method <c>BookingFee</c>: ciało z klasy bazowej wklejone do Total.</summary>
    private class NaivePricing
    {
        public decimal Total(Ticket ticket)
        {
            return new Training.Workshop.M4.S06InlineMethod.Step2.TicketPricing().Price(ticket)
                + 0.00m;
        }

        protected virtual decimal BookingFee()
        {
            return 0.00m;
        }
    }

    private sealed class NaiveOnlinePricing : NaivePricing
    {
        protected override decimal BookingFee()
        {
            return 2.00m;
        }
    }
}
