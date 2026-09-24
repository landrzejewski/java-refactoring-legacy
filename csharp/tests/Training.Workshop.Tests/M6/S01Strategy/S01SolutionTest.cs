using Training.Workshop.M6.S01Strategy.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S01Strategy;

/// <summary>Kontrakt rozwiązania i pułapka "momentu wyboru" strategii.</summary>
public sealed class S01SolutionTest
{
    [Fact]
    public void StrategiesAreSharedBecauseTheyAreStateless()
    {
        Assert.Same(DiscountPrograms.ForName("STANDARD"), DiscountPrograms.ForName("STANDARD"));
    }

    [Fact]
    public void NewProgramIsJustAnotherStrategyWithoutTouchingTheContext()
    {
        var blackFriday = new TicketPricer(new BlackFridayDiscount());
        Assert.Equal(Money.Of("20.00"), blackFriday.Price(Money.Of("40.00"), "N"));
    }

    [Fact]
    public void BeforeStep3BaseIsValidatedBeforeTheProgram()
    {
        var step2 = new Training.Workshop.M6.S01Strategy.Step2.TicketPricer();
        var error = Assert.Throws<ArgumentException>(
            () => step2.Price(Money.Of("-1.00"), "N", "BLACK_FRIDAY"));
        Assert.Equal("base price must not be negative", error.Message);
    }

    [Fact]
    public void ChoosingInConstructorMovesTheUnknownProgramErrorEarlier()
    {
        var error = Assert.Throws<ArgumentException>(
            () => new TicketPricer(DiscountPrograms.ForName("BLACK_FRIDAY")).Price(Money.Of("-1.00"), "N"));
        Assert.Equal("unknown program: BLACK_FRIDAY", error.Message);
    }

    /// <summary>Nowy program zniżek (w Javie lambda) - kontekst TicketPricer bez zmian.</summary>
    private sealed class BlackFridayDiscount : IDiscountPolicy
    {
        public Money Discount(Money basePrice, string ticketType) => basePrice.Percent(50);
    }
}
