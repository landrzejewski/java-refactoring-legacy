using Training.Workshop.M6.S02Polymorphism;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S02Polymorphism;

/// <summary>Jeden test kontraktowy dla wszystkich rodzajów seansu i wszystkich kroków.</summary>
public sealed class S02EquivalenceTest
{
    private static readonly Scene<ScreeningRow, string> Scene = Support.Scene.Variants<ScreeningRow, string>()
        .Variant("start", row => Run(() =>
        {
            var s = Training.Workshop.M6.S02Polymorphism.Start.Screening.FromRow(row);
            return s.Label() + "|" + s.DurationMinutes() + "|" + s.Price();
        }))
        .Variant("step1", row => Run(() =>
        {
            var s = Training.Workshop.M6.S02Polymorphism.Step1.Screening.FromRow(row);
            return s.Label() + "|" + s.DurationMinutes() + "|" + s.Price();
        }))
        .Variant("step2", row => Run(() =>
        {
            var s = Training.Workshop.M6.S02Polymorphism.Step2.Screening.FromRow(row);
            return s.Label() + "|" + s.DurationMinutes() + "|" + s.Price();
        }))
        .Variant("step3", row => Run(() =>
        {
            var s = Training.Workshop.M6.S02Polymorphism.Step3.Screening.FromRow(row);
            return s.Label() + "|" + s.DurationMinutes() + "|" + s.Price();
        }))
        .Expect("zwykły seans", new ScreeningRow("REGULAR", "Amator", 120), "Amator|140|25.00")
        .Expect("premiera", new ScreeningRow("PREMIERE", "Diuna", 166), "Premiera: Diuna|196|35.00")
        .Expect("maraton 3 filmy", new ScreeningRow("MARATHON", "Wladca Pierscieni", 3),
            "Maraton: Wladca Pierscieni (3 filmy)|390|60.00")
        .Expect("maraton 1 film - bez przerw", new ScreeningRow("MARATHON", "Diuna", 1),
            "Maraton: Diuna (1 filmy)|120|20.00")
        .Expect("nieznany rodzaj", new ScreeningRow("DRIVE_IN", "Amator", 120),
            "ERROR: unknown screening kind: DRIVE_IN");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepDescribesScreeningsTheSame(string test) => Scene.Run(test);

    private static string Run(Func<string> description)
    {
        try
        {
            return description();
        }
        catch (ArgumentException exception)
        {
            return "ERROR: " + exception.Message;
        }
    }
}
