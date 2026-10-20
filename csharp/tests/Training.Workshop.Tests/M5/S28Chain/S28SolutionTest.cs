using System.Reflection;
using Training.Workshop.M5.S28Chain;
using Training.Workshop.M5.S28Chain.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S28Chain;

/// <summary>Łańcuch: jawnie składany, ogniwo zatrzymuje albo przekazuje - w odróżnieniu od dekoratora.</summary>
public sealed class S28SolutionTest
{
    private static readonly DateTime Start = new(2026, 10, 20, 18, 0, 0);

    private const BindingFlags DeclaredFields =
        BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.DeclaredOnly;

    [Fact]
    public void ChainIsAssembledExplicitlySoAFestivalCanSkipAutoRefunds()
    {
        var festival = new RefundApprover(
            new AfterScreeningHandler(new CashierHandler(Money.Of("100.00"), new NoApproverHandler())));
        Assert.Equal("APPROVED CASHIER", festival.Decide(Request("50.00", Start.AddDays(-3))).ToString());
        Assert.Equal("REJECTED brak uprawnionego zatwierdzajacego",
            festival.Decide(Request("500.00", Start.AddDays(-3))).ToString());
    }

    [Fact]
    public void HandlerThatDecidesStopsTheChainUnlikeADecorator()
    {
        var spy = new SpyHandler();
        var cashier = new CashierHandler(Money.Of("100.00"), spy);

        Assert.Equal("APPROVED CASHIER", cashier.Handle(Request("40.00", Start.AddHours(-2))).ToString());
        Assert.Equal(0, spy.Calls); // obsłużone żądanie nie idzie dalej

        Assert.Equal("REJECTED spy", cashier.Handle(Request("400.00", Start.AddHours(-2))).ToString());
        Assert.Equal(1, spy.Calls); // nieobsłużone żądanie idzie dokładnie do następnika
    }

    [Theory]
    [InlineData(typeof(AfterScreeningHandler))]
    [InlineData(typeof(AutoRefundHandler))]
    [InlineData(typeof(CashierHandler))]
    [InlineData(typeof(ManagerHandler))]
    public void EveryLinkKnowsOnlyItsSuccessor(Type link)
    {
        var successors = link.GetFields(DeclaredFields).Count(field => field.FieldType == typeof(IRefundHandler));
        Assert.Equal(1, successors);
    }

    [Fact]
    public void EndOfTheChainHasNoSuccessor()
    {
        Assert.Empty(typeof(NoApproverHandler).GetFields(DeclaredFields));
    }

    private static RefundRequest Request(string amount, DateTime requestedAt)
    {
        return new RefundRequest(Money.Of(amount), Start, requestedAt);
    }

    private sealed class SpyHandler : IRefundHandler
    {
        public int Calls { get; private set; }

        public RefundDecision Handle(RefundRequest request)
        {
            Calls++;
            return RefundDecision.Rejected("spy");
        }
    }
}
