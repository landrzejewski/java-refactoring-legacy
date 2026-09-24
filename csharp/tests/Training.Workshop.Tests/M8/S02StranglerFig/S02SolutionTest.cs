using Training.Workshop.M8.S02StranglerFig;

namespace Training.Workshop.Tests.M8.S02StranglerFig;

/// <summary>Routing fasady krok po kroku i dowód, że stary system zniknął.</summary>
public sealed class S02SolutionTest
{
    [Fact]
    public void FacadeRoutesMoreOperationsToNewCodeWithEveryStep()
    {
        var ledger = new BookingLedger();
        Assert.Equal(new Dictionary<string, string> { ["book"] = "legacy", ["report"] = "legacy" },
            new Training.Workshop.M8.S02StranglerFig.Step1.CinemaFacade(ledger).Routes());
        Assert.Equal(new Dictionary<string, string> { ["book"] = "new", ["report"] = "legacy" },
            new Training.Workshop.M8.S02StranglerFig.Step2.CinemaFacade(ledger).Routes());
        Assert.Equal(new Dictionary<string, string> { ["book"] = "new", ["report"] = "new" },
            new Training.Workshop.M8.S02StranglerFig.Step3.CinemaFacade(ledger).Routes());
    }

    [Fact]
    public void LegacyReportSeesBookingsTakenByTheNewModule()
    {
        var ledger = new BookingLedger();
        var facade = new Training.Workshop.M8.S02StranglerFig.Step2.CinemaFacade(ledger);
        facade.Book("anna@kino.pl", "Amator", 1, 1, false);
        Assert.Equal("""
            RAPORT
            Amator: 1 bil., 25.00
            Biletow: 1
            Przychod z biletow: 25.00
            Oplaty rezerwacyjne: 0.00

            """, facade.Report());
    }

    [Fact]
    public void LegacyCinemaIsDeletedInTheLastStep()
    {
        var assembly = typeof(Training.Workshop.M8.S02StranglerFig.Step4.CinemaFacade).Assembly;
        Assert.Null(assembly.GetType("Training.Workshop.M8.S02StranglerFig.Step4.LegacyCinema"));
    }
}
