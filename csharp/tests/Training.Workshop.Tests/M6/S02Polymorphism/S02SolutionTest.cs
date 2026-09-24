using System.Diagnostics;
using Training.Workshop.M6.S02Polymorphism;
using Training.Workshop.M6.S02Polymorphism.Step3;

namespace Training.Workshop.Tests.M6.S02Polymorphism;

/// <summary>
/// Po refaktoryzacji każdy rodzaj ma własne dane, a switch klienta obsługuje wszystkie rodzaje.
/// C# nie ma zamkniętych hierarchii sprawdzanych przez kompilator (jak <c>sealed interface</c> w Javie),
/// więc switch po typach potrzebuje ramienia <c>_</c>, a listę rodzajów pilnuje test.
/// </summary>
public sealed class S02SolutionTest
{
    [Fact]
    public void MappingCreatesTheRightSubtypeWithNamedData()
    {
        var marathon = Screening.FromRow(new ScreeningRow("MARATHON", "Diuna", 2));
        Assert.Equal(new MarathonScreening("Diuna", 2), marathon);
    }

    [Fact]
    public void ClientSwitchIsExhaustiveWithoutDefault()
    {
        // w Javie dodanie czwartego rodzaju psuje kompilację switcha klienta - to zaleta i koszt;
        // w C# ten sam sygnał daje dopiero ta asercja: nowy podtyp Screening = czerwony test
        var kinds = typeof(Screening).Assembly.GetTypes()
            .Where(type => type.IsSubclassOf(typeof(Screening)) && !type.IsAbstract)
            .Select(type => type.Name)
            .Order();
        Assert.Equal(["MarathonScreening", "PremiereScreening", "RegularScreening"], kinds);
        Assert.Equal("zwykly", Badge(new RegularScreening("Amator", 120)));
        Assert.Equal("premiera", Badge(new PremiereScreening("Diuna", 166)));
        Assert.Equal("maraton", Badge(new MarathonScreening("Diuna", 2)));
    }

    private static string Badge(Screening screening)
    {
        return screening switch
        {
            RegularScreening => "zwykly",
            PremiereScreening => "premiera",
            MarathonScreening => "maraton",
            _ => throw new UnreachableException("nieznany rodzaj seansu: " + screening),
        };
    }
}
