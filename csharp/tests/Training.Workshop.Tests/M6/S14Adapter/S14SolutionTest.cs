using Training.Workshop.M6.S14Adapter;
using Training.Workshop.M6.S14Adapter.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S14Adapter;

/// <summary>Adapter tłumaczy jednostki; serwis da się testować bez żadnej bramki.</summary>
public sealed class S14SolutionTest
{
    [Fact]
    public void XmlAdapterSendsAmountInGrosze()
    {
        var recording = new RecordingXmlPayGateway();
        new XmlPayAdapter(recording).Pay("R1", Money.Of("40.00"));
        Assert.Equal(["<charge ref='R1' amount='4000'/>"], recording.Sent);
    }

    [Fact]
    public void CheckoutWorksWithAnyGateway()
    {
        var gateways = new Dictionary<string, IPaymentGateway> { ["FAKE"] = new FakeGateway() };
        Assert.Equal("FAKE-R9", new CheckoutService(gateways)
            .Pay("FAKE", "R9", Money.Of("25.00")).TransactionId);
    }

    private sealed class RecordingXmlPayGateway : XmlPayGateway
    {
        public List<string> Sent { get; } = [];

        public override string Submit(string xml)
        {
            Sent.Add(xml);
            return base.Submit(xml);
        }
    }

    private sealed class FakeGateway : IPaymentGateway
    {
        public PaymentResult Pay(string reservationId, Money amount) => PaymentResult.Accepted("FAKE-" + reservationId);
    }
}
