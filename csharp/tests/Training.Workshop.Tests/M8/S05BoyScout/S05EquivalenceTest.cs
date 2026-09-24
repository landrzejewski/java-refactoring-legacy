using Training.Workshop.M8.S05BoyScout;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S05BoyScout;

/// <summary>
/// Test równoważności: start i poprawna poprawa (Step2). Krok 1 celowo NIE jest tu wariantem -
/// to nadużycie, które S05SolutionTest demaskuje.
/// </summary>
public sealed class S05EquivalenceTest
{
    internal static readonly Ticket Regular = new("Amator", new DateTime(2026, 3, 14, 18, 0, 0),
        ["C5"], "jan@kino.pl", "600100200", 25.00);
    internal static readonly Ticket Rows9And10 = new("Diuna", new DateTime(2026, 3, 13, 20, 0, 0),
        ["A9", "A10"], "anna@kino.pl", "600100300", 84.00);
    internal static readonly Ticket NoPhone = new("Kraina Lodu", new DateTime(2026, 3, 14, 10, 30, 0),
        ["B1", "B2"], "ola@kino.pl", null, 47.20);
    internal static readonly Ticket MixedCaseEmail = new("Amator", new DateTime(2026, 3, 14, 18, 0, 0),
        ["D7"], " Anna@Kino.pl ", "600100400", 25.00);

    private static readonly Scene<Ticket, string> Scene = Support.Scene.Variants<Ticket, string>()
        .Variant("start", new Training.Workshop.M8.S05BoyScout.Start.TicketPrinter().Print)
        .Variant("step2", new Training.Workshop.M8.S05BoyScout.Step2.TicketPrinter().Print)
        .Expect("zwykły bilet", Regular, """
            Film: Amator
            Seans: 2026-03-14 18:00
            Miejsca: C5
            Klient: jan@kino.pl
            Tel: 600100200
            Do zaplaty: 25.00

            """)
        .Expect("miejsca w rzędach 9 i 10", Rows9And10, """
            Film: Diuna
            Seans: 2026-03-13 20:00
            Miejsca: A9, A10
            Klient: anna@kino.pl
            Tel: 600100300
            Do zaplaty: 84.00

            """)
        .Expect("brak telefonu", NoPhone, """
            Film: Kraina Lodu
            Seans: 2026-03-14 10:30
            Miejsca: B1, B2
            Klient: ola@kino.pl
            Tel: -
            Do zaplaty: 47.20

            """)
        .Expect("e-mail z wielkimi literami", MixedCaseEmail, """
            Film: Amator
            Seans: 2026-03-14 18:00
            Miejsca: D7
            Klient: Anna@Kino.pl
            Tel: 600100400
            Do zaplaty: 25.00

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void BoyScoutCleanupKeepsTheTicketIdentical(string test) => Scene.Run(test);
}
