using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S19Visitor;

/// <summary>Paragon (linie, suma, VAT) identyczny: is, Visitor i switch expression po typach.</summary>
public sealed class S19EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", code => new Training.Workshop.M6.S19Visitor.Start.ReceiptPrinter()
            .Print(new Training.Workshop.M6.S19Visitor.Start.SampleOrders().Find(code)))
        .Variant("step1", code => new Training.Workshop.M6.S19Visitor.Step1.ReceiptPrinter()
            .Print(new Training.Workshop.M6.S19Visitor.Step1.SampleOrders().Find(code)))
        .Variant("step2", code => new Training.Workshop.M6.S19Visitor.Step2.ReceiptPrinter()
            .Print(new Training.Workshop.M6.S19Visitor.Step2.SampleOrders().Find(code)))
        .Variant("step3", code => new Training.Workshop.M6.S19Visitor.Step3.ReceiptPrinter()
            .Print(new Training.Workshop.M6.S19Visitor.Step3.SampleOrders().Find(code)))
        .Expect("wieczór: bilet, bar, voucher", "evening", Text("""
            Bilet Diuna IMAX 40.00
            Popcorn L 18.00
            Cola 9.00
            Voucher KINO20 -20.00
            Razem: 47.00
            VAT: 8.01
            """))
        .Expect("voucher prawie pokrywa bilet", "voucher", Text("""
            Bilet Amator 2D 25.00
            Voucher KINO20 -20.00
            Razem: 5.00
            VAT: 1.85
            """))
        .Expect("puste zamówienie", "empty", "Razem: 0.00\nVAT: 0.00\n");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepPrintsTheSameReceipt(string test) => Scene.Run(test);

    /// <summary>Jak Javowy text block: znaki końca linii LF i końcowy znak nowej linii.</summary>
    private static string Text(string block) => block.ReplaceLineEndings("\n") + "\n";
}
