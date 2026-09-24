using Training.Workshop.M6.S12OneMany;
using Training.Workshop.M6.S12OneMany.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S12OneMany;

/// <summary>Composite pozwala zagnieżdżać grupy - potrącenie wciąż jest naliczane raz na zwrot.</summary>
public sealed class S12SolutionTest
{
    private static readonly DateTime Now = new(2026, 10, 2, 12, 0, 0);

    [Fact]
    public void NestedGroupsAreRefundedAsOneRequest()
    {
        var imax = new SingleTicket(new TicketData(Money.Of("40.00"), Now.AddDays(3)));
        var family = new TicketGroup([
            new SingleTicket(new TicketData(Money.Of("25.00"), Now.AddDays(3))),
            new SingleTicket(new TicketData(Money.Of("25.00"), Now.AddDays(3)))]);
        Assert.Equal(Money.Of("87.00"), new RefundService().Refund(new TicketGroup([imax, family]), Now));
    }

#pragma warning disable CS0618 // celowo wywołujemy przestarzałe delegacje z kroku 2
    [Fact]
    public void DeprecatedWrappersInStep2DelegateToTheNewContract()
    {
        var service = new Training.Workshop.M6.S12OneMany.Step2.RefundService();
        var ticket = new TicketData(Money.Of("40.00"), Now.AddDays(3));
        Assert.Equal(Money.Of("37.00"), service.Refund(ticket, Now));
        Assert.Equal(Money.Of("77.00"), service.RefundAll([ticket, ticket], Now));
    }
#pragma warning restore CS0618
}
