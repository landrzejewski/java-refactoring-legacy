using System.Reflection;
using Training.Workshop.M5.S30Facade;
using Training.Workshop.M5.S30Facade.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S30Facade;

/// <summary>Krok 3: podsystem schowany, kanały znają tylko fasadę, niezmiennik trzyma się także przy awarii.</summary>
public sealed class S30SolutionTest
{
    private const string Step3 = "Training.Workshop.M5.S30Facade.Step3.";

    private static readonly TicketOrder Timeout = new("S1", ["A1"], "N", "TIMEOUT", "anna@example.com");

    [Fact]
    public void SubsystemIsHiddenBehindTheFacade()
    {
        foreach (var name in new[] { "SeatRegistry", "PriceList", "PaymentClient", "TicketMailer" })
        {
            Assert.False(typeof(Money).Assembly.GetType(Step3 + name, throwOnError: true)!.IsPublic, name);
        }
        Assert.Empty(typeof(BookingFacade).GetConstructors()); // fasadę składa korzeń kompozycji
    }

    [Fact]
    public void SalesChannelsKnowOnlyTheFacade()
    {
        foreach (var channel in new[] { typeof(OnlineShop), typeof(BoxOffice) })
        {
            var dependencies = channel
                .GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic)
                .Select(field => field.FieldType)
                .ToList();
            Assert.Equal([typeof(BookingFacade)], dependencies);
        }
    }

    [Fact]
    public void GatewayFailureNoLongerLeavesSeatsTaken()
    {
        var cinema = new Cinema();
        Assert.Throws<InvalidOperationException>(() => cinema.OnlineShop.Buy(Timeout));
        Assert.Equal("miejsca={S1=[]} platnosci=[] maile=[]", cinema.State());
    }

    [Fact]
    public void BeforeStep3AGatewayFailureLeakedSeats()
    {
        var cinema = new Training.Workshop.M5.S30Facade.Step2.Cinema();
        Assert.Throws<InvalidOperationException>(() => cinema.OnlineShop.Buy(Timeout));
        Assert.Equal("miejsca={S1=[A1]} platnosci=[] maile=[]", cinema.State());
    }
}
