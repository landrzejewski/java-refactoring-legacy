using Training.Workshop.M6.S17Singleton.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S17Singleton;

/// <summary>Pomiar przed decyzją o cyklu życia i testowalność po wstrzyknięciu.</summary>
[Collection("Legacy")]
public sealed class S17SolutionTest
{
    /// <summary>
    /// Pomiar w Start (licznik instancji); po "warsztat.sh jump" licznika nie ma i test kończy się
    /// bez sprawdzeń (xUnit 2 nie ma odpowiednika assumeTrue).
    /// </summary>
    [Fact]
    public void StartCreatesAPriceListForEveryQuote()
    {
        var created = typeof(Money).Assembly
            .GetType("Training.Workshop.M6.S17Singleton.Start.PriceList")?
            .GetMethod("Created", Type.EmptyTypes);
        if (created == null)
        {
            return; // Start nie ma już licznika instancji
        }
        var before = (int)created.Invoke(null, null)!;
        var desk = new Training.Workshop.M6.S17Singleton.Start.TicketDesk();
        desk.Quote("2D", false);
        desk.Quote("3D", false);
        desk.Quote("IMAX", true);
        Assert.Equal(3, (int)created.Invoke(null, null)! - before);
    }

    [Fact]
    public void SingletonReturnsTheSameInstance()
    {
        Assert.Same(Training.Workshop.M6.S17Singleton.Step1.PriceList.GetInstance(),
            Training.Workshop.M6.S17Singleton.Step1.PriceList.GetInstance());
    }

    [Fact]
    public void InjectedTariffNeedsNoGlobalState()
    {
        var promo = new TicketDesk(new PromoTariff());
        Assert.Equal(Money.Of("21.00"), promo.Quote("IMAX", true));
        Assert.Equal(Money.Of("42.00"), new TicketDesk().Quote("IMAX", true)); // domyślny cennik bez zmian
    }

    private sealed class PromoTariff : ITariff
    {
        public Money BasePrice(string format) => Money.Of("19.00");
    }
}
