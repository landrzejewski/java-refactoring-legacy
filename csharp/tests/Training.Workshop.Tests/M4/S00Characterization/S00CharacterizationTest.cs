using System.Globalization;
using System.Text.RegularExpressions;
using Microsoft.Extensions.Time.Testing;
using Training.Workshop.M4.S00Characterization;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M4.S00Characterization;

/// <summary>
/// Test charakterystyki generatora potwierdzeń - zapisuje, co kod ROBI dziś.
/// Powstaje w trzech ruchach (patrz przewodnik):
/// A) Start: "scrubber" maskuje linię z bieżącym czasem, kulturę ustawiamy jawnie;
/// B) Step1: wstrzyknięty TimeProvider pozwala porównać cały dokument bez maskowania;
/// C) Step2: ten sam test chroni pierwszą refaktoryzację.
/// Nazwy przypadków z "ZASTANE" opisują dziwne zachowanie, którego NIE poprawiamy w refaktoryzacji.
/// </summary>
public sealed class S00CharacterizationTest
{
    private static readonly CultureInfo ServerCulture = CultureInfo.GetCultureInfo("pl-PL");

    private static readonly Booking ImaxOnline = new("  anna@kino.pl ", "Diuna", 3,
        new TimeOnly(20, 0), ["N", "S"], true);
    private static readonly Booking Morning3D = new("jan@kino.pl", "Kraina Lodu", 2,
        new TimeOnly(11, 0), ["E", "C"], false);
    private static readonly Booking TenTickets = new("jan@kino.pl", "Amator", 1,
        new TimeOnly(18, 30), Enumerable.Repeat("N", 10).ToList(), true);
    private static readonly Booking ElevenTickets = new("jan@kino.pl", "Amator", 1,
        new TimeOnly(18, 30), Enumerable.Repeat("N", 11).ToList(), false);

    private static readonly Scene<Booking, string> Scene = Support.Scene.Variants<Booking, string>()
        .Variant("start", b => Scrubbed(new Training.Workshop.M4.S00Characterization.Start
            .BookingConfirmation().Confirm, b))
        .Variant("step1", b => Scrubbed(new Training.Workshop.M4.S00Characterization.Step1
            .BookingConfirmation(Fixed()).Confirm, b))
        .Variant("step2", b => Scrubbed(new Training.Workshop.M4.S00Characterization.Step2
            .BookingConfirmation(Fixed()).Confirm, b))
        .Expect("IMAX wieczorem, online, klient z odstępami", ImaxOnline, """
            POTWIERDZENIE REZERWACJI
            Klient: ANNA@KINO.PL
            Film: Diuna, IMAX, 20:00
            Bilety: 2 [N, S]
            Bilety razem: 70,00
            Oplata rezerwacyjna: 4,00
            Do zaplaty: 74,00
            Wygenerowano: <czas>

            """)
        .Expect("3D rano, senior i dziecko, kasa", Morning3D, """
            POTWIERDZENIE REZERWACJI
            Klient: JAN@KINO.PL
            Film: Kraina Lodu, 3D, 11:00
            Bilety: 2 [E, C]
            Bilety razem: 31,60
            Oplata rezerwacyjna: 0,00
            Do zaplaty: 31,60
            Wygenerowano: <czas>

            """)
        .Expect("ZASTANE: 10 biletów bez rabatu grupowego (reguła mówi 10+)", TenTickets, """
            POTWIERDZENIE REZERWACJI
            Klient: JAN@KINO.PL
            Film: Amator, 2D, 18:30
            Bilety: 10 [N, N, N, N, N, N, N, N, N, N]
            Bilety razem: 250,00
            Oplata rezerwacyjna: 20,00
            Do zaplaty: 270,00
            Wygenerowano: <czas>

            """)
        .Expect("11 biletów - rabat grupowy 10%", ElevenTickets, """
            POTWIERDZENIE REZERWACJI
            Klient: JAN@KINO.PL
            Film: Amator, 2D, 18:30
            Bilety: 11 [N, N, N, N, N, N, N, N, N, N, N]
            Bilety razem: 247,50
            Oplata rezerwacyjna: 0,00
            Do zaplaty: 247,50
            Wygenerowano: <czas>

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryVariantPrintsTheApprovedDocument(string test) => Scene.Run(test);

    [Fact]
    public void StartPrintsCurrentTimeSoTheTestMustScrubIt()
    {
        var document = InCulture(ServerCulture, () => new Training.Workshop.M4.S00Characterization
            .Start.BookingConfirmation().Confirm(ImaxOnline));
        Assert.Matches(new Regex(@"\nWygenerowano: \d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2})?\n\z"), document);
    }

    [Fact]
    public void FromStep1TheWholeDocumentIsDeterministic()
    {
        foreach (var variant in new List<Func<Booking, string>>
        {
            new Training.Workshop.M4.S00Characterization.Step1.BookingConfirmation(Fixed()).Confirm,
            new Training.Workshop.M4.S00Characterization.Step2.BookingConfirmation(Fixed()).Confirm,
        })
        {
            var document = InCulture(ServerCulture, () => variant(ImaxOnline));
            Assert.EndsWith("Do zaplaty: 74,00\nWygenerowano: 2026-09-23T08:15:30\n", document);
        }
    }

    [Fact]
    public void FoundDuringCharacterizationAmountsDependOnServerLocale()
    {
        var document = InCulture(CultureInfo.GetCultureInfo("en-US"), () => new Training.Workshop.M4
            .S00Characterization.Step2.BookingConfirmation(Fixed()).Confirm(ImaxOnline));
        Assert.Contains("Do zaplaty: 74.00\n", document);
    }

    /// <summary>Zegar zatrzymany na 2026-09-23T08:15:30Z, strefa lokalna UTC.</summary>
    private static FakeTimeProvider Fixed()
    {
        var clock = new FakeTimeProvider(DateTimeOffset.Parse("2026-09-23T08:15:30Z", CultureInfo.InvariantCulture));
        clock.SetLocalTimeZone(TimeZoneInfo.Utc);
        return clock;
    }

    private static string Scrubbed(Func<Booking, string> variant, Booking booking)
    {
        var document = InCulture(ServerCulture, () => variant(booking));
        return Regex.Replace(document, "Wygenerowano: .*\n", "Wygenerowano: <czas>\n");
    }

    /// <summary>Ustawia bieżącą kulturę tylko na czas wywołania - test nie zależy od maszyny.</summary>
    private static string InCulture(CultureInfo culture, Func<string> action)
    {
        var previous = CultureInfo.CurrentCulture;
        var previousUi = CultureInfo.CurrentUICulture;
        CultureInfo.CurrentCulture = culture;
        CultureInfo.CurrentUICulture = culture;
        try
        {
            return action();
        }
        finally
        {
            CultureInfo.CurrentCulture = previous;
            CultureInfo.CurrentUICulture = previousUi;
        }
    }
}
