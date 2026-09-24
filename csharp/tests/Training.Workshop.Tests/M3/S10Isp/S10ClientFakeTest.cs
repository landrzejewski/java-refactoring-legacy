namespace Training.Workshop.Tests.M3.S10Isp;

/// <summary>
/// ISP widziany z testu klienta: ile trzeba zaimplementować, żeby przetestować kasę?
/// Gruby ICinemaAdminService (jak w Start; tu z kroku 1, bo Start jest edytowany na żywo)
/// - osiem metod, sześć "nie dotyczy". Rola ITicketSales - dwie.
/// </summary>
public sealed class S10ClientFakeTest
{
    [Fact]
    public void FatInterfaceForcesAFakeOfTheWholeBackOffice()
    {
        var fake = new FakeBackOffice();
        Assert.Equal("bilet T-9: Amator, miejsce 4",
            new Training.Workshop.M3.S10Isp.Step1.CashDesk(fake).Sell("Amator", 4));
    }

    [Fact]
    public void Step2CashDeskNeedsOnlyItsRole()
    {
        var fake = new FakeTicketSales();
        Assert.Equal("bilet T-9: Amator, miejsce 4",
            new Training.Workshop.M3.S10Isp.Step2.CashDesk(fake).Sell("Amator", 4));
    }

    private sealed class FakeBackOffice : Training.Workshop.M3.S10Isp.Step1.ICinemaAdminService
    {
        public string SellTicket(string title, int seat) => "T-9";
        public string RefundTicket(string ticketId) => "zwrot " + ticketId;
        public decimal DailyRevenue() => throw new NotSupportedException();
        public int TicketsSold(string title) => throw new NotSupportedException();
        public void ScheduleScreening(string title, TimeOnly start) => throw new NotSupportedException();
        public void CancelScreening(string title) => throw new NotSupportedException();
        public IReadOnlyList<string> Screenings() => throw new NotSupportedException();
        public void UpdateTicketPrice(decimal price) => throw new NotSupportedException();
    }

    private sealed class FakeTicketSales : Training.Workshop.M3.S10Isp.Step2.ITicketSales
    {
        public string SellTicket(string title, int seat) => "T-9";
        public string RefundTicket(string ticketId) => "zwrot " + ticketId;
    }
}
