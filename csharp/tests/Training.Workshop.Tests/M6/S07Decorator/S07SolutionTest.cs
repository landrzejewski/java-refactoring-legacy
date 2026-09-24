using Training.Workshop.M6.S07Decorator;
using Training.Workshop.M6.S07Decorator.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S07Decorator;

/// <summary>Granice przezroczystości dekoratora: is, Equals, kolejność.</summary>
public sealed class S07SolutionTest
{
    private readonly Ticket _core = new("Diuna", "IMAX", Money.Of("40.00"));

    [Fact]
    public void InstanceofSeesOnlyTheOutermostDecorator()
    {
        var ticket = new TicketAssembler().Assemble(
            new TicketOrder("Diuna", "IMAX", Money.Of("40.00"), true, false, true));
        Assert.True(ticket is Insurance);
        Assert.False(ticket is VipSeat, "VIP jest ukryty wewnątrz - pytanie 'czy VIP?' wymaga innego API");
    }

    [Fact]
    public void DecoratedTicketIsNotEqualToItsCoreDespiteSameTitle()
    {
        Assert.NotEqual<IPricedTicket>(_core, new VipSeat(_core));
    }

    [Fact]
    public void OrderOfDecoratorsChangesDescriptionButNotPrice()
    {
        IPricedTicket a = new Insurance(new VipSeat(_core));
        IPricedTicket b = new VipSeat(new Insurance(_core));
        Assert.Equal(a.Price(), b.Price());
        Assert.NotEqual(a.Description(), b.Description());
        Assert.NotEqual(a, b);
    }
}
