using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M6.S17Singleton;

/// <summary>
/// Wejście "FORMAT kanał": wycena bez zmian niezależnie od liczby instancji cennika.
/// Kolekcja Legacy: Start zmienia statyczny licznik mierzony w S17SolutionTest.
/// </summary>
[Collection("Legacy")]
public sealed class S17EquivalenceTest
{
    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", Safe(new Training.Workshop.M6.S17Singleton.Start.TicketDesk().Quote))
        .Variant("step1", Safe(new Training.Workshop.M6.S17Singleton.Step1.TicketDesk().Quote))
        .Variant("step2", Safe(new Training.Workshop.M6.S17Singleton.Step2.TicketDesk().Quote))
        .Variant("step3", Safe(new Training.Workshop.M6.S17Singleton.Step3.TicketDesk().Quote))
        .Expect("2D kasa", "2D kasa", "25.00")
        .Expect("3D online", "3D online", "34.00")
        .Expect("IMAX online", "IMAX online", "42.00")
        .Expect("nieznany format", "4DX kasa", "ERROR unknown format: 4DX");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepQuotesTheSame(string test) => Scene.Run(test);

    private static Func<string, string> Safe(Func<string, bool, Money> quote)
    {
        return input =>
        {
            var parts = input.Split(' ');
            try
            {
                return quote(parts[0], parts[1] == "online").ToString();
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }
}
