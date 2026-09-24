using System.Globalization;

namespace Training.Workshop.Tests.M3.S15Invariants;

/// <summary>
/// Ścieżka przez serwis działa identycznie we wszystkich wariantach (także komunikaty błędów).
/// Ścieżka importu: do kroku 1 przepuszcza złe dane (pułapka), w kroku 2 je odrzuca.
/// </summary>
public sealed class S15InvariantsTest
{
    public sealed record Request(string Email, int Seats, string Total);

    private static readonly Support.Scene<Request, string> Bookings = Support.Scene.Variants<Request, string>()
        .Variant("start", r => Outcome(() => new Training.Workshop.M3.S15Invariants.Start.BookingService()
            .Book(r.Email, r.Seats, Parse(r.Total))))
        .Variant("step1", r => Outcome(() => new Training.Workshop.M3.S15Invariants.Step1.BookingService()
            .Book(r.Email, r.Seats, Parse(r.Total))))
        .Variant("step2", r => Outcome(() => new Training.Workshop.M3.S15Invariants.Step2.BookingService()
            .Book(r.Email, r.Seats, Parse(r.Total))))
        .Expect("poprawna rezerwacja", new Request("anna@kino.pl", 2, "50.00"),
            "zarezerwowano: anna@kino.pl, miejsc 2, kwota 50.00")
        .Expect("zly email", new Request("jan-kino.pl", 1, "25.00"), "blad: niepoprawny email: jan-kino.pl")
        .Expect("zero miejsc", new Request("jan@kino.pl", 0, "0.00"),
            "blad: liczba miejsc musi byc dodatnia: 0")
        .Expect("ujemna kwota", new Request("jan@kino.pl", 1, "-5.00"), "blad: kwota nie moze byc ujemna: -5.00");

    private static readonly Support.Scene<string, string> Imports = Support.Scene.Variants<string, string>()
        .Variant("start", new Training.Workshop.M3.S15Invariants.Start.ReservationImport().ImportLine)
        .Variant("step1", new Training.Workshop.M3.S15Invariants.Step1.ReservationImport().ImportLine)
        .Variant("step2", new Training.Workshop.M3.S15Invariants.Step2.ReservationImport().ImportLine)
        .Expect("poprawny wiersz", "anna@kino.pl;2;50.00", "zaimportowano: anna@kino.pl, miejsc 2, kwota 50.00");

    public static TheoryData<string> BookingCases => Bookings.Tests();

    public static TheoryData<string> ImportCases => Imports.Tests();

    [Theory]
    [MemberData(nameof(BookingCases))]
    public void BookingServiceBehavesTheSame(string test) => Bookings.Run(test);

    [Theory]
    [MemberData(nameof(ImportCases))]
    public void ImportOfValidLineBehavesTheSame(string test) => Imports.Run(test);

    [Fact]
    public void ImportWithoutInvariantsCreatesInvalidReservation()
    {
        // krok 1 = Start pod tym względem (Start jest edytowany na żywo, więc sprawdzamy kopię)
        Assert.Equal("zaimportowano: jan-kino.pl, miejsc 0, kwota -5.00",
            new Training.Workshop.M3.S15Invariants.Step1.ReservationImport().ImportLine("jan-kino.pl;0;-5.00"));
    }

    [Fact]
    public void Step2ImportCannotCreateInvalidReservation()
    {
        var importer = new Training.Workshop.M3.S15Invariants.Step2.ReservationImport();
        var error = Assert.Throws<ArgumentException>(() => importer.ImportLine("jan-kino.pl;0;-5.00"));
        Assert.Equal("niepoprawny email: jan-kino.pl", error.Message);
    }

    private static string Outcome(Func<string> action)
    {
        try
        {
            return action();
        }
        catch (ArgumentException e)
        {
            return "blad: " + e.Message;
        }
    }

    private static decimal Parse(string amount) => decimal.Parse(amount, CultureInfo.InvariantCulture);
}
