using System.Globalization;
using Training.Workshop.M3.S07Srp;

namespace Training.Workshop.Tests.M3.S07Srp;

/// <summary>Podział raportu według aktorów nie zmienia ani jednego znaku dokumentu.</summary>
public sealed class S07EquivalenceTest
{
    private static readonly Support.Scene<IReadOnlyList<Sale>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<Sale>, string>()
            .Variant("start", new Training.Workshop.M3.S07Srp.Start.DailyReport().Render)
            .Variant("step1", new Training.Workshop.M3.S07Srp.Step1.DailyReport().Render)
            .Variant("step2", new Training.Workshop.M3.S07Srp.Step2.DailyReport().Render)
            .Variant("step3", new Training.Workshop.M3.S07Srp.Step3.DailyReport().Render)
            .Expect("trzy seanse, Diuna dwa razy",
                [Sale("Diuna", 2, "80.00", "18.00"),
                    Sale("Amator", 3, "75.00", "12.00"),
                    Sale("Diuna", 1, "40.00", "0.00")],
                """
                KSIEGOWOSC
                Bilety brutto 195.00, netto 180.56
                Bar brutto 30.00, netto 24.39
                Razem brutto 225.00
                MARKETING
                Hit dnia: Diuna (138.00)
                Sprzedanych biletow: 6

                """)
            .Expect("remis - wygrywa pierwszy alfabetycznie",
                [Sale("Kraina Lodu", 1, "20.00", "5.00"), Sale("Amator", 1, "25.00", "0.00")],
                """
                KSIEGOWOSC
                Bilety brutto 45.00, netto 41.67
                Bar brutto 5.00, netto 4.07
                Razem brutto 50.00
                MARKETING
                Hit dnia: Amator (25.00)
                Sprzedanych biletow: 2

                """)
            .Expect("dzien bez sprzedazy", [],
                """
                KSIEGOWOSC
                Bilety brutto 0.00, netto 0.00
                Bar brutto 0.00, netto 0.00
                Razem brutto 0.00
                MARKETING
                Hit dnia: brak
                Sprzedanych biletow: 0

                """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepRendersTheSameReport(string test) => Scene.Run(test);

    private static Sale Sale(string title, int tickets, string ticketRevenue, string barRevenue) =>
        new(title, tickets, decimal.Parse(ticketRevenue, CultureInfo.InvariantCulture),
            decimal.Parse(barRevenue, CultureInfo.InvariantCulture));
}
