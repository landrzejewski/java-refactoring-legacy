using Training.Workshop.M8.S02StranglerFig;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M8.S02StranglerFig;

/// <summary>
/// Test równoważności: ten sam scenariusz klienta (rezerwacje + raport) daje ten sam zapis
/// niezależnie od tego, czy operacje obsługuje legacy, nowy moduł czy mieszanka obu.
/// </summary>
public sealed class S02EquivalenceTest
{
    private static readonly Scene<bool, string> Scene = Support.Scene.Variants<bool, string>()
        .Variant("start", withBookings => Run(
            ledger => new Training.Workshop.M8.S02StranglerFig.Start.LegacyCinema(ledger), withBookings))
        .Variant("step1", withBookings => Run(
            ledger => new Training.Workshop.M8.S02StranglerFig.Step1.CinemaFacade(ledger), withBookings))
        .Variant("step2", withBookings => Run(
            ledger => new Training.Workshop.M8.S02StranglerFig.Step2.CinemaFacade(ledger), withBookings))
        .Variant("step3", withBookings => Run(
            ledger => new Training.Workshop.M8.S02StranglerFig.Step3.CinemaFacade(ledger), withBookings))
        .Variant("step4", withBookings => Run(
            ledger => new Training.Workshop.M8.S02StranglerFig.Step4.CinemaFacade(ledger), withBookings))
        .Expect("rezerwacje (w tym błędna) i raport", true, """
            B1
            B2
            ERROR: no seats
            B3
            RAPORT
            Amator: 10 bil., 225.00
            Diuna: 2 bil., 80.00
            Kraina Lodu: 1 bil., 32.00
            Biletow: 13
            Przychod z biletow: 337.00
            Oplaty rezerwacyjne: 6.00

            """)
        .Expect("raport pustej bazy", false, """
            RAPORT
            Biletow: 0
            Przychod z biletow: 0.00
            Oplaty rezerwacyjne: 0.00

            """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void ClientsSeeTheSameSystemAfterEveryStep(string test) => Scene.Run(test);

    private static string Run(Func<BookingLedger, ICinemaApi> system, bool withBookings)
    {
        ICinemaApi api = system(new BookingLedger());
        if (!withBookings)
        {
            return api.Report();
        }
        return api.Book("anna@kino.pl", "Diuna", 3, 2, true) + "\n"
            + api.Book("jan@kino.pl", "Amator", 1, 10, false) + "\n"
            + api.Book("ola@kino.pl", "Kraina Lodu", 2, 0, true) + "\n"
            + api.Book("ola@kino.pl", "Kraina Lodu", 2, 1, true) + "\n"
            + api.Report();
    }
}
