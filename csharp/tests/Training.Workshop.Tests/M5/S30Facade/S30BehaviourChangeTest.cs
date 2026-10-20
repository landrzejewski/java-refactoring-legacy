using System.Reflection;

namespace Training.Workshop.Tests.M5.S30Facade;

/// <summary>
/// Sens sceny: kopia orkiestracji w kasie zapomniała zwolnić miejsca po odmowie płatności.
/// Fasada w kroku 2 to naprawia - to świadoma zmiana zachowania, nie "czysta" refaktoryzacja.
/// </summary>
public sealed class S30BehaviourChangeTest
{
    private static readonly IReadOnlyList<string> DeclinedAtDesk = [
        "desk S1 A1 N 10000 jan@example.com", "state", "online S1 A1 N 4111 anna@example.com"];

    private static readonly string Leak = """
        ERROR payment declined
        miejsca={S1=[A1]} platnosci=[DECLINED 10000 25.00] maile=[]
        ERROR seat taken A1
        """.TrimEnd().ReplaceLineEndings("\n");

    private static readonly string Released = """
        ERROR payment declined
        miejsca={S1=[]} platnosci=[DECLINED 10000 25.00] maile=[]
        OK 27.00
        """.TrimEnd().ReplaceLineEndings("\n");

    /// <summary>
    /// Po "warsztat.sh jump" do kroku 2+ kasa nie ma już własnej orkiestracji i test kończy się
    /// bez sprawdzeń (xUnit 2 nie ma odpowiednika assumeTrue).
    /// </summary>
    [Fact]
    public void StartBoxOfficeLeaksSeatsAfterDeclinedPayment()
    {
        var ownOrchestration = typeof(Training.Workshop.M5.S30Facade.Start.BoxOffice)
            .GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic)
            .Any(field => field.FieldType.Name == "SeatRegistry");
        if (!ownOrchestration)
        {
            return; // Start kasy korzysta już z fasady
        }
        var cinema = new Training.Workshop.M5.S30Facade.Start.Cinema();
        Assert.Equal(Leak, Play(S30EquivalenceTest.Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell,
            cinema.State)));
    }

    [Fact]
    public void Step1StillLeaksBecauseOnlyTheShopUsesTheFacade()
    {
        var cinema = new Training.Workshop.M5.S30Facade.Step1.Cinema();
        Assert.Equal(Leak, Play(S30EquivalenceTest.Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell,
            cinema.State)));
    }

    [Fact]
    public void Step2FacadeReleasesSeatsForBothChannels()
    {
        var cinema = new Training.Workshop.M5.S30Facade.Step2.Cinema();
        Assert.Equal(Released, Play(S30EquivalenceTest.Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell,
            cinema.State)));
    }

    [Fact]
    public void Step3FacadeReleasesSeatsForBothChannels()
    {
        var cinema = new Training.Workshop.M5.S30Facade.Step3.Cinema();
        Assert.Equal(Released, Play(S30EquivalenceTest.Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell,
            cinema.State)));
    }

    private static string Play(Func<string, string> handle)
    {
        return string.Join("\n", DeclinedAtDesk.Select(handle).ToList());
    }
}
