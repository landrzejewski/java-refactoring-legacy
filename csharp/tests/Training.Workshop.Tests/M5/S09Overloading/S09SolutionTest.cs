using Training.Workshop.Shared;
using Start = Training.Workshop.M5.S09Overloading.Start;
using Step1 = Training.Workshop.M5.S09Overloading.Step1;
using Step2 = Training.Workshop.M5.S09Overloading.Step2;

namespace Training.Workshop.Tests.M5.S09Overloading;

/// <summary>Overloading (wybór statyczny) kontra overriding (dyspozycja dynamiczna) - wynik każdego wariantu.</summary>
public sealed class S09SolutionTest
{
    private static readonly Money Imax = Money.Of("40.00");
    private static readonly Money Standard2D = Money.Of("25.00");

    [Fact]
    public void StartPicksOverloadByDeclaredType()
    {
        Start.Ticket declaredAsBase = new Start.StudentTicket("Amator", Standard2D);
        // pułapka: Price(Ticket)
        Assert.Equal(Money.Of("25.00"), new Start.PriceList().Price(declaredAsBase));
        IReadOnlyList<Start.Ticket> cart = [new Start.Ticket("Diuna", Imax), new Start.StudentTicket("Amator", Standard2D)];
        // student zapłacił pełną cenę
        Assert.Equal(Money.Of("65.00"), new Start.Checkout().Total(cart));
    }

    [Fact]
    public void OverridingDispatchesOnRuntimeClass()
    {
        IReadOnlyList<Step1.Ticket> cart = [new Step1.Ticket("Diuna", Imax), new Step1.StudentTicket("Amator", Standard2D)];
        Assert.Equal(Money.Of("58.75"), new Step1.Checkout().Total(cart));
    }

    [Fact]
    public void OverloadedEqualsIsInvisibleToCollections()
    {
        IReadOnlyList<Start.Ticket> cart = [new Start.Ticket("Diuna", Imax)];
        var same = new Start.Ticket("Diuna", Imax);
        Assert.True(cart[0].Equals(same), "wywołanie z typem Ticket wybiera przeciążenie");
        Assert.False(new Start.Checkout().AlreadyInCart(cart, same), "pułapka: Contains() woła Equals(object)");
        Assert.False(new Step1.Checkout().AlreadyInCart([new Step1.Ticket("Diuna", Imax)], new Step1.Ticket("Diuna", Imax)));
    }

    [Fact]
    public void SolutionOverridesEqualsAndHashCode()
    {
        IReadOnlyList<Step2.Ticket> cart = [new Step2.Ticket("Diuna", Imax)];
        Assert.True(new Step2.Checkout().AlreadyInCart(cart, new Step2.Ticket("Diuna", Imax)));
        Assert.False(new Step2.Checkout().AlreadyInCart(cart, new Step2.StudentTicket("Diuna", Imax)));
        Assert.Equal(new Step2.Ticket("Diuna", Imax).GetHashCode(), new Step2.Ticket("Diuna", Imax).GetHashCode());
    }
}
