namespace Training.Workshop.Tests.M6.S04EncapsulateFactory;

/// <summary>Po kroku 3 publiczne są tylko ITicket i fabryka Tickets.</summary>
public sealed class S04SolutionTest
{
    private const string Step3 = "Training.Workshop.M6.S04EncapsulateFactory.Step3.Ticketing.";

    [Fact]
    public void SolutionHidesConcreteTicketClassesBehindTheFactory()
    {
        var assembly = typeof(Training.Workshop.M6.S04EncapsulateFactory.Step3.BoxOffice).Assembly;
        Assert.True(Type("StandardTicket").IsNestedPrivate);
        Assert.True(Type("VipTicket").IsNestedPrivate);
        Assert.True(assembly.GetType(Step3 + "Tickets", true)!.IsPublic);
        Assert.True(assembly.GetType(Step3 + "ITicket", true)!.IsPublic);

        Type Type(string name) => assembly.GetType(Step3 + "Tickets+" + name, true)!;
    }
}
