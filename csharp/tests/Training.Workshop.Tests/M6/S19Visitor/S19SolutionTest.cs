using System.Diagnostics;
using Training.Workshop.M6.S19Visitor.Step2;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S19Visitor;

/// <summary>Macierz zmian: nowa operacja jest tania zarówno jako Visitor, jak i jako switch po typach.</summary>
public sealed class S19SolutionTest
{
    [Fact]
    public void NewOperationIsANewVisitor()
    {
        var vatGroup = new VatGroupVisitor();
        IReadOnlyList<IOrderItem> items = [new TicketItem("Diuna", "IMAX", Money.Of("40.00")),
            new SnackItem("Cola", Money.Of("9.00")), new VoucherItem("KINO20", Money.Of("20.00"))];
        Assert.Equal(["B", "A", "-"], items.Select(item => item.Accept(vatGroup)).ToList());
    }

    /// <summary>Odpowiednik newOperationIsANewSwitchInJava25 - w C# switch expression z ramieniem "_".</summary>
    [Fact]
    public void NewOperationIsANewSwitchExpression()
    {
        IReadOnlyList<Training.Workshop.M6.S19Visitor.Step3.IOrderItem> items = [
            new Training.Workshop.M6.S19Visitor.Step3.TicketItem("Diuna", "IMAX", Money.Of("40.00")),
            new Training.Workshop.M6.S19Visitor.Step3.VoucherItem("KINO20", Money.Of("20.00"))];
        Assert.Equal(["B", "-"], items.Select(item => item switch
        {
            Training.Workshop.M6.S19Visitor.Step3.TicketItem => "B",
            Training.Workshop.M6.S19Visitor.Step3.SnackItem => "A",
            Training.Workshop.M6.S19Visitor.Step3.VoucherItem => "-",
            _ => throw new UnreachableException(),
        }).ToList());
    }

    private sealed class VatGroupVisitor : IOrderItemVisitor<string>
    {
        public string VisitTicket(TicketItem ticket) => "B";

        public string VisitSnack(SnackItem snack) => "A";

        public string VisitVoucher(VoucherItem voucher) => "-";
    }
}
