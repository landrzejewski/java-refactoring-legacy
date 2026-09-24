using Training.Workshop.M8.S03ParallelRun;
using Training.Workshop.Shared;
using static Training.Workshop.Tests.M8.S03ParallelRun.S03EquivalenceTest;

namespace Training.Workshop.Tests.M8.S03ParallelRun;

/// <summary>Co wiemy po każdym kroku trybu shadow i co zmienia przełączenie.</summary>
public sealed class S03SolutionTest
{
    private static readonly IReadOnlyList<TicketQuery> Traffic =
        [ImaxNormal, ImaxStudentVip, Morning3DChild, Morning3DNormalVip, Evening2DSenior, Morning2DStudent, Unknown4DX];

    [Fact]
    public void Step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches()
    {
        var service = new Training.Workshop.M8.S03ParallelRun.Step1.PriceService();
        Replay(query => service.Price(query));
        // klient dostaje wynik legacy
        Assert.Equal(Money.Of("0.00"), service.Price(Unknown4DX));
        // 3 różnice w ruchu + powtórzony 4DX, ale nie wiemy które
        Assert.Equal(4, service.Mismatches);
    }

    [Fact]
    public void Step2ReportShowsWhatDivergedAndWhy()
    {
        var report = new Training.Workshop.M8.S03ParallelRun.Step2.VerificationReport();
        var service = new Training.Workshop.M8.S03ParallelRun.Step2.PriceService(report);
        Replay(query => service.Price(query));
        Assert.Equal(
            [
                "ROZBIEZNOSC 3D CHILD 10:30 rzad 3: legacy 17.20, kandydat 19.20",
                "ROZBIEZNOSC 2D STUDENT 09:00 rzad 2: legacy 13.75, kandydat 15.00",
                "BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: ArgumentException: Nieznany format: 4DX",
            ],
            report.Problems());
        Assert.Equal(Traffic.Count, report.Entries().Count);
    }

    [Fact]
    public void Step3FixedCandidateLeavesOnlyTheAcceptedDifference()
    {
        var report = new Training.Workshop.M8.S03ParallelRun.Step3.VerificationReport();
        var service = new Training.Workshop.M8.S03ParallelRun.Step3.PriceService(report);
        Replay(query => service.Price(query));
        Assert.Equal(
            ["BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: ArgumentException: Nieznany format: 4DX"],
            report.Problems());
    }

    [Fact]
    public void Step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat()
    {
        var report = new Training.Workshop.M8.S03ParallelRun.Step4.VerificationReport();
        var service = new Training.Workshop.M8.S03ParallelRun.Step4.PriceService(
            Training.Workshop.M8.S03ParallelRun.Step4.MigrationMode.Candidate, report);
        Assert.Equal(Money.Of("17.20"), service.Price(Morning3DChild));
        Assert.Throws<ArgumentException>(() => service.Price(Unknown4DX));
        // po przełączeniu cień już nie działa
        Assert.Empty(report.Entries());
    }

    private static void Replay(Action<TicketQuery> service)
    {
        foreach (var query in Traffic)
        {
            service(query);
        }
    }
}
