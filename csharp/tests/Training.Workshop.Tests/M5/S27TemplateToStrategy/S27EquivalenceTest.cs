using System.Globalization;
using Training.Workshop.M5.S27TemplateToStrategy;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S27TemplateToStrategy;

/// <summary>
/// Wszystkie cztery kombinacje okres x format na tych samych danych, granice tygodnia
/// (niedziela przed, poniedziałek po), dzień bez sprzedaży i kolejność błędów wyboru.
/// </summary>
public sealed class S27EquivalenceTest
{
    public sealed record Request(string Period, string Format, DateOnly Day);

    private static readonly IReadOnlyList<ScreeningSale> Sales =
    [
        Sale("2026-10-18", "Amator", 4, "100.00"),
        Sale("2026-10-19", "Diuna", 10, "400.00"),
        Sale("2026-10-20", "Diuna", 5, "200.00"),
        Sale("2026-10-20", "Kraina Lodu", 3, "81.00"),
        Sale("2026-10-20", "Szybcy; reedycja", 1, "25.00"),
        Sale("2026-10-25", "Amator", 2, "50.00"),
        Sale("2026-10-26", "Diuna", 7, "280.00"),
    ];

    private static readonly Scene<Request, string> Scene =
        Support.Scene.Variants<Request, string>()
            .Variant("start", Render((p, f) => Training.Workshop.M5.S27TemplateToStrategy.Start.Reports.Of(p, f).Render))
            .Variant("step1", Render((p, f) => Training.Workshop.M5.S27TemplateToStrategy.Step1.Reports.Of(p, f).Render))
            .Variant("step2", Render((p, f) => Training.Workshop.M5.S27TemplateToStrategy.Step2.Reports.Of(p, f).Render))
            .Variant("step3", Render((p, f) => Training.Workshop.M5.S27TemplateToStrategy.Step3.Reports.Of(p, f).Render))
            .Expect("dzień CSV (escapowanie średnika)", Req("DAILY", "CSV", "2026-10-20"), Text("""
                # raport dzien 2026-10-20
                film;bilety;przychod
                Diuna;5;200.00
                Kraina Lodu;3;81.00
                "Szybcy; reedycja";1;25.00
                SUMA;9;306.00
                """))
            .Expect("dzień tekst", Req("DAILY", "TEXT", "2026-10-20"), Text("""
                RAPORT DZIEN 2026-10-20
                -------------------------------------
                Diuna                    5     200.00
                Kraina Lodu              3      81.00
                Szybcy; reedycja         1      25.00
                -------------------------------------
                RAZEM                    9     306.00
                """))
            .Expect("tydzień CSV ze środy (sumy po tytule)", Req("WEEKLY", "CSV", "2026-10-22"), Text("""
                # raport tydzien 2026-10-19..2026-10-25
                film;bilety;przychod
                Amator;2;50.00
                Diuna;15;600.00
                Kraina Lodu;3;81.00
                "Szybcy; reedycja";1;25.00
                SUMA;21;756.00
                """))
            .Expect("tydzień tekst z niedzieli (ten sam tydzień)", Req("WEEKLY", "TEXT", "2026-10-25"), Text("""
                RAPORT TYDZIEN 2026-10-19..2026-10-25
                -------------------------------------
                Amator                   2      50.00
                Diuna                   15     600.00
                Kraina Lodu              3      81.00
                Szybcy; reedycja         1      25.00
                -------------------------------------
                RAZEM                   21     756.00
                """))
            .Expect("tydzień od poniedziałku: następny tydzień", Req("WEEKLY", "CSV", "2026-10-26"), Text("""
                # raport tydzien 2026-10-26..2026-11-01
                film;bilety;przychod
                Diuna;7;280.00
                SUMA;7;280.00
                """))
            .Expect("dzień bez sprzedaży", Req("DAILY", "CSV", "2026-10-21"), Text("""
                # raport dzien 2026-10-21
                film;bilety;przychod
                SUMA;0;0.00
                """))
            .Expect("nieznany okres", Req("MONTHLY", "CSV", "2026-10-20"), "ERROR unknown period: MONTHLY")
            .Expect("nieznany format", Req("DAILY", "PDF", "2026-10-20"), "ERROR unknown format: PDF")
            .Expect("najpierw okres, potem format", Req("MONTHLY", "PDF", "2026-10-20"),
                "ERROR unknown period: MONTHLY");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepRendersEveryCombinationTheSame(string test) => Scene.Run(test);

    /// <summary>Wspólny kształt Reports.Of(okres, format).Render wszystkich wariantów.</summary>
    private delegate Func<DateOnly, IReadOnlyList<ScreeningSale>, string> ReportFactory(string period, string format);

    private static Func<Request, string> Render(ReportFactory reports)
    {
        return request =>
        {
            try
            {
                return reports(request.Period, request.Format)(request.Day, Sales);
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }

    private static Request Req(string period, string format, string day)
    {
        return new Request(period, format, DateOnly.Parse(day, CultureInfo.InvariantCulture));
    }

    private static ScreeningSale Sale(string day, string title, int tickets, string revenue)
    {
        return new ScreeningSale(DateOnly.Parse(day, CultureInfo.InvariantCulture), title, tickets, Money.Of(revenue));
    }

    /// <summary>Jak Javowy text block: znaki końca linii LF i końcowy znak nowej linii.</summary>
    private static string Text(string block) => block.ReplaceLineEndings("\n") + "\n";
}
