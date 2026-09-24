using Training.Workshop.M7.S11MiddleMan;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S11MiddleMan;

/// <summary>Test równoważności obu klientów pośrednika - także dla nieznanego seansu.</summary>
public sealed class S11EquivalenceTest
{
    private static readonly ScreeningCatalog Catalog = new(
    [
        new Screening("S1", "Diuna", "IMAX", 120),
        new Screening("S2", "Kraina Lodu", "3D", 0),
        new Screening("S3", "Amator", "2D", 7),
    ]);

    private const string Board = "S1 Diuna IMAX\nS2 Kraina Lodu 3D\nS3 Amator 2D";

    private static readonly Scene<string, string> Scene = BuildScene();

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void ClientsSeeTheSameThing(string test) => Scene.Run(test);

    [Fact]
    public void CatalogItselfRejectsUnknownScreening()
    {
        var error = Assert.Throws<KeyNotFoundException>(() => Catalog.FreeSeats("S9"));
        Assert.Equal("brak seansu S9", error.Message);
    }

    private static Scene<string, string> BuildScene()
    {
        var startFacade = new Training.Workshop.M7.S11MiddleMan.Start.CinemaFacade(Catalog);
        var step1Facade = new Training.Workshop.M7.S11MiddleMan.Step1.CinemaFacade(Catalog);
        var step2Facade = new Training.Workshop.M7.S11MiddleMan.Step2.CinemaFacade(Catalog);
        return Support.Scene.Variants<string, string>()
            .Variant("start", id => new Training.Workshop.M7.S11MiddleMan.Start.SeatBadge(startFacade).Badge(id)
                + " | " + new Training.Workshop.M7.S11MiddleMan.Start.DailyBoard(startFacade).Render())
            .Variant("step1", id => new Training.Workshop.M7.S11MiddleMan.Step1.SeatBadge(step1Facade).Badge(id)
                + " | " + new Training.Workshop.M7.S11MiddleMan.Step1.DailyBoard(step1Facade).Render())
            .Variant("step2", id => new Training.Workshop.M7.S11MiddleMan.Step2.SeatBadge(Catalog).Badge(id)
                + " | " + new Training.Workshop.M7.S11MiddleMan.Step2.DailyBoard(step2Facade).Render())
            .Variant("step3", id => new Training.Workshop.M7.S11MiddleMan.Step3.SeatBadge(Catalog).Badge(id)
                + " | " + new Training.Workshop.M7.S11MiddleMan.Step3.DailyBoard(Catalog).Render())
            .Expect("wolne miejsca", "S1", "Diuna (IMAX): 120 wolnych | " + Board)
            .Expect("wyprzedane", "S2", "S2: WYPRZEDANE | " + Board)
            .Expect("nieznany seans wyglada jak wyprzedany (historyczne zachowanie)", "S9",
                "S9: WYPRZEDANE | " + Board);
    }
}
