using Training.Workshop.M6.S18CollectingParameter;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S18CollectingParameter;

/// <summary>Te same ostrzeżenia, w tej samej kolejności i z tym samym separatorem.</summary>
public sealed class S18EquivalenceTest
{
    private static readonly DateTime Show = new(2026, 10, 2, 18, 0, 0);
    private static readonly DateTime Before = Show.AddHours(-2);

    private static readonly Scene<ReservationDraft, string> Scene = Support.Scene.Variants<ReservationDraft, string>()
        .Variant("start", new Training.Workshop.M6.S18CollectingParameter.Start.ReservationValidator().Validate)
        .Variant("step1", new Training.Workshop.M6.S18CollectingParameter.Step1.ReservationValidator().Validate)
        .Variant("step2", new Training.Workshop.M6.S18CollectingParameter.Step2.ReservationValidator().Validate)
        .Variant("step3", new Training.Workshop.M6.S18CollectingParameter.Step3.ReservationValidator().Validate)
        .Expect("poprawna", new ReservationDraft("anna@kino.pl", ["A1", "A2"], Show, Before), "OK")
        .Expect("brak e-maila i miejsc", new ReservationDraft(" ", [], Show, Before),
            "brak e-maila; brak miejsc")
        .Expect("null e-mail", new ReservationDraft(null, ["A1"], Show, Before), "brak e-maila")
        .Expect("zły e-mail, duplikaty zgłoszone raz, po starcie",
            new ReservationDraft("jan.kino.pl", ["A1", "A2", "A1", "A1", "A2"], Show, Show),
            "niepoprawny e-mail: jan.kino.pl; miejsce A1 zdublowane; miejsce A2 zdublowane; "
                + "seans juz sie rozpoczal")
        .Expect("grupa 10+", new ReservationDraft("jan@kino.pl",
            ["B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "B10"], Show, Before),
            "grupa 10+: zastosuj rabat grupowy");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepCollectsTheSameWarnings(string test) => Scene.Run(test);
}
