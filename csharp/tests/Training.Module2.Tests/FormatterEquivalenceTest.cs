namespace Training.Module2.Tests;

public sealed class FormatterEquivalenceTest
{
    private readonly LegacyInvoiceFormatter legacy = new();
    private readonly InvoiceFormatter refactored = new();

    [Theory]
    [MemberData(nameof(RepresentativeInvoices))]
    public void RefactoringPreservesObservedOutput(
        string? customer,
        IReadOnlyList<InvoiceLine> lines)
    {
        Assert.Equal(
            legacy.Format(customer, lines),
            refactored.Format(customer, lines));
    }

    public static TheoryData<string?, IReadOnlyList<InvoiceLine>> RepresentativeInvoices() => new()
    {
        { "Acme", [] },
        { null, [new InvoiceLine("BOOK", 1, 10.00m)] },
        { "vip", [new InvoiceLine("A", 3, 0.10m), new InvoiceLine("B", 2, 19.995m)] },
    };
}
