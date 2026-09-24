namespace Training.Workshop.Tests.Legacy;

/// <summary>
/// Golden master całego starego systemu. Każda zmiana w legacy, która
/// zmienia choćby jedną wiadomość, kwotę czy kolejność, zostanie wykryta.
/// Zatwierdzony wynik jest wspólny z Javą: src/test/resources/workshop/cinema-manager.approved.txt
/// </summary>
[Collection("Legacy")]
public sealed class CinemaManagerGoldenMasterTest
{
    [Fact]
    public void OneDayOfCinemaProducesApprovedOutput()
    {
        Assert.Equal(Approved(), CinemaManagerScript.Run());
    }

    private static string Approved()
    {
        return File.ReadAllText(Path.Combine(AppContext.BaseDirectory, "Legacy", "cinema-manager.approved.txt"));
    }
}
