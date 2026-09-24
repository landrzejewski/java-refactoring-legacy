using Training.Workshop.M8.S09Codemod;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S09Codemod;

/// <summary>
/// Na prostym przypadku (jedno wywołanie w jednej linii) wszystkie wersje codemodu się zgadzają.
/// Różnice wychodzą dopiero na trudnych przypadkach - patrz S09SolutionTest.
/// </summary>
public sealed class S09EquivalenceTest
{
    internal const string Simple = """
        using Cinema;

        namespace Desk;

        public class Kiosk
        {
            string Sell(BookingService bookings, string[] seats, string[] types)
            {
                return bookings.Book("S3", "anna@kino.pl", seats, types, true, false);
            }
        }

        """;

    private static readonly Scene<string, string> Scene = Support.Scene.Variants<string, string>()
        .Variant("start", s => Describe(new Training.Workshop.M8.S09Codemod.Start.BookCallCodemod(
            SampleProject.Api).FindLines(s), new Training.Workshop.M8.S09Codemod.Start.BookCallCodemod(
            SampleProject.Api).Rewrite(s)))
        .Variant("step1", s => Describe(new Training.Workshop.M8.S09Codemod.Step1.BookCallCodemod(
            SampleProject.Api).FindLines(s), new Training.Workshop.M8.S09Codemod.Step1.BookCallCodemod(
            SampleProject.Api).Rewrite(s)))
        .Variant("step2", s => Describe(new Training.Workshop.M8.S09Codemod.Step2.BookCallCodemod(
            SampleProject.Api).FindLines(s), new Training.Workshop.M8.S09Codemod.Step2.BookCallCodemod(
            SampleProject.Api).Rewrite(s)))
        .Variant("step3", s => Describe(new Training.Workshop.M8.S09Codemod.Step3.BookCallCodemod(
            SampleProject.Api).FindLines(s), new Training.Workshop.M8.S09Codemod.Step3.BookCallCodemod(
            SampleProject.Api).Rewrite(s)))
        .Expect("jedno wywołanie z literałami", Simple, """
            linie: [9]
            using Cinema;
            using Cinema.Options;

            namespace Desk;

            public class Kiosk
            {
                string Sell(BookingService bookings, string[] seats, string[] types)
                {
                    return bookings.Book("S3", "anna@kino.pl", seats, types, Channel.Web, Glasses.Rented);
                }
            }

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryVersionHandlesTheSimpleCase(string test) => Scene.Run(test);

    private static string Describe(IReadOnlyList<int> lines, string rewritten)
    {
        return "linie: [" + string.Join(", ", lines) + "]\n" + rewritten;
    }
}
