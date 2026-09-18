namespace Training.Module1.Tests;

public sealed class LegacyOrderServiceTest
{
    [Fact]
    public void PlacesVipOrderAndInvokesExternalCollaborators()
    {
        RecordingOrderRepository repository = new();
        RecordingMailGateway mailGateway = new();
        LegacyOrderService service = new(repository, mailGateway);
        Order order = new(
            Guid.Parse("9aa026a4-fc39-4af8-a008-d9b831b0ba59"),
            "customer@example.com",
            [new OrderLine("BOOK-1", 2, 100.00m)]);

        Receipt receipt = service.PlaceOrder(order, "VIP", false, "PL");

        Assert.Equal(236.39m, receipt.Total);
        Assert.Equal(receipt.Total, repository.SavedTotal);
        Assert.Equal("Order total: 236.39", mailGateway.SentMessage);
    }

    private sealed class RecordingOrderRepository : IOrderRepository
    {
        public decimal? SavedTotal { get; private set; }

        public void Save(Guid orderId, decimal total) => SavedTotal = total;
    }

    private sealed class RecordingMailGateway : IMailGateway
    {
        public string? SentMessage { get; private set; }

        public void Send(string recipient, string body) => SentMessage = body;
    }
}
