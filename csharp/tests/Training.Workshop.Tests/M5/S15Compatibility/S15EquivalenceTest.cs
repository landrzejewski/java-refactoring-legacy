using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S15Compatibility;

/// <summary>
/// Test równoważności na poziomie ŹRÓDŁA: ten sam kod klienta, skompilowany z każdym wariantem,
/// daje ten sam eksport i tę samą wycenę. Zgodność binarną sprawdza S15SolutionTest.
/// </summary>
public sealed class S15EquivalenceTest
{
    private static readonly Scene<string, string> Export = Scene.Variants<string, string>()
        .Variant("start", kind => new Training.Workshop.M5.S15Compatibility.Start.TicketExporter().Export(kind == "STUDENT"
            ? new Training.Workshop.M5.S15Compatibility.Start.StudentTicket("Amator", Money.Of("25.00"))
            : new Training.Workshop.M5.S15Compatibility.Start.StandardTicket("Diuna", Money.Of("40.00"))))
        .Variant("step1", kind => new Training.Workshop.M5.S15Compatibility.Step1.TicketExporter().Export(kind == "STUDENT"
            ? new Training.Workshop.M5.S15Compatibility.Step1.StudentTicket("Amator", Money.Of("25.00"))
            : new Training.Workshop.M5.S15Compatibility.Step1.StandardTicket("Diuna", Money.Of("40.00"))))
        .Variant("step2", kind => new Training.Workshop.M5.S15Compatibility.Step2.TicketExporter().Export(kind == "STUDENT"
            ? new Training.Workshop.M5.S15Compatibility.Step2.StudentTicket("Amator", Money.Of("25.00"))
            : new Training.Workshop.M5.S15Compatibility.Step2.StandardTicket("Diuna", Money.Of("40.00"))))
        .Variant("step3", kind => new Training.Workshop.M5.S15Compatibility.Step3.TicketExporter().Export(kind == "STUDENT"
            ? new Training.Workshop.M5.S15Compatibility.Step3.StudentTicket("Amator", Money.Of("25.00"))
            : new Training.Workshop.M5.S15Compatibility.Step3.StandardTicket("Diuna", Money.Of("40.00"))))
        .Variant("step4", kind => new Training.Workshop.M5.S15Compatibility.Step4.TicketExporter().Export(kind == "STUDENT"
            ? new Training.Workshop.M5.S15Compatibility.Step4.StudentTicket("Amator", Money.Of("25.00"))
            : new Training.Workshop.M5.S15Compatibility.Step4.StandardTicket("Diuna", Money.Of("40.00"))))
        .Expect("studencki", "STUDENT", "Amator;cena=18.75")
        .Expect("normalny", "NORMAL", "Diuna;cena=40.00");

#pragma warning disable CS0618 // step4: świadome wywołanie przestarzałego przeciążenia
    private static readonly Scene<string, string> Quote = Scene.Variants<string, string>()
        .Variant("start", p => new Training.Workshop.M5.S15Compatibility.Start.BoxOfficeApi()
            .Quote(new Training.Workshop.M5.S15Compatibility.Start.StudentTicket("Amator", Money.Of(p))).ToString())
        .Variant("step1", p => new Training.Workshop.M5.S15Compatibility.Step1.BoxOfficeApi()
            .Quote(new Training.Workshop.M5.S15Compatibility.Step1.StudentTicket("Amator", Money.Of(p))).ToString())
        .Variant("step2", p => new Training.Workshop.M5.S15Compatibility.Step2.BoxOfficeApi()
            .Quote(new Training.Workshop.M5.S15Compatibility.Step2.StudentTicket("Amator", Money.Of(p))).ToString())
        .Variant("step3", p => new Training.Workshop.M5.S15Compatibility.Step3.BoxOfficeApi()
            .Quote(new Training.Workshop.M5.S15Compatibility.Step3.StudentTicket("Amator", Money.Of(p))).ToString())
        .Variant("step4", p => new Training.Workshop.M5.S15Compatibility.Step4.BoxOfficeApi()
            .Quote(new Training.Workshop.M5.S15Compatibility.Step4.StudentTicket("Amator", Money.Of(p))).ToString())
        .Expect("studencki 2D", "25.00", "18.75");
#pragma warning restore CS0618

    public static TheoryData<string> ExportCases => Export.Tests();

    public static TheoryData<string> QuoteCases => Quote.Tests();

    [Theory]
    [MemberData(nameof(ExportCases))]
    public void EveryStepExportsTheSameRow(string test) => Export.Run(test);

    [Theory]
    [MemberData(nameof(QuoteCases))]
    public void EveryStepQuotesStudentTicketTheSameWay(string test) => Quote.Run(test);
}
