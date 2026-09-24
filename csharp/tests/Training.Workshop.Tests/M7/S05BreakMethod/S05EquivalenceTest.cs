using Training.Workshop.M7.S05BreakMethod;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S05BreakMethod;

/// <summary>
/// Test równoważności: ten sam tekst, ten sam wyjątek (typ i komunikat)
/// i nietknięta lista wejściowa w start i w każdym kroku.
/// </summary>
public sealed class S05EquivalenceTest
{
    private static readonly Screening Diuna = new("Diuna", "IMAX", new TimeOnly(20, 0), 1, false);
    private static readonly Screening Kraina = new("Kraina Lodu", "3D", new TimeOnly(11, 0), 2, false);
    private static readonly Screening Amator = new("Amator", "2D", new TimeOnly(18, 30), 3, false);
    private static readonly Screening AmatorLate = new("Amator", "2D", new TimeOnly(21, 0), 3, true);
    private static readonly Screening Alien = new("Alien", "2D", new TimeOnly(20, 0), 4, false);

    private static readonly Scene<IReadOnlyList<Screening?>, string> Scene = Support.Scene
        .Variants<IReadOnlyList<Screening?>, string>()
        .Variant("start", Observe(new Training.Workshop.M7.S05BreakMethod.Start.RepertoireBuilder().Build))
        .Variant("step1", Observe(new Training.Workshop.M7.S05BreakMethod.Step1.RepertoireBuilder().Build))
        .Variant("step2", Observe(new Training.Workshop.M7.S05BreakMethod.Step2.RepertoireBuilder().Build))
        .Variant("step3", Observe(new Training.Workshop.M7.S05BreakMethod.Step3.RepertoireBuilder().Build))
        .Expect("sortowanie po godzinie, potem tytule; odwolany pominiety",
            [Diuna, Kraina, AmatorLate, Amator, Alien],
            "REPERTUAR\n"
                + "11:00 Kraina Lodu (3D), sala 2\n"
                + "18:30 Amator (2D), sala 3\n"
                + "20:00 Alien (2D), sala 4\n"
                + "20:00 Diuna (IMAX), sala 1\n"
                + "| wejscie bez zmian: true")
        .Expect("same odwolane",
            [AmatorLate],
            "REPERTUAR\n"
                + "brak seansow\n"
                + "| wejscie bez zmian: true")
        .Expect("null w srodku listy",
            [Diuna, null, Kraina],
            "ArgumentException: screening must not be null | wejscie bez zmian: true");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepBuildsTheSameRepertoire(string test) => Scene.Run(test);

    /// <summary>Wektor zachowania: wynik albo wyjątek, plus to, czy lista klienta nie została zmieniona.</summary>
    private static Func<IReadOnlyList<Screening?>, string> Observe(Func<IReadOnlyList<Screening?>, string> build)
    {
        return input =>
        {
            var clientList = new List<Screening?>(input);
            var before = new List<Screening?>(clientList);
            string result;
            try
            {
                result = build(clientList);
            }
            catch (ArgumentException e)
            {
                result = "ArgumentException: " + e.Message + " ";
            }
            return result + "| wejscie bez zmian: " + (before.SequenceEqual(clientList) ? "true" : "false");
        };
    }
}
