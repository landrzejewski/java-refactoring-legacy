namespace Training.Workshop.Tests.M4.S01Rename;

/// <summary>Granice automatycznego Rename: nazwy, których IDE nie widzi jako użyć symbolu.</summary>
public sealed class S01RenameLimitsTest
{
    [Fact]
    public void ReflectionTurnsCSharpNamesIntoTheCsvHeader()
    {
        Assert.Equal("t;n;d", Header(typeof(Training.Workshop.M4.S01Rename.Start.SalesReport.Line)));
        // po Rename refleksja dałaby nowy nagłówek - dlatego Step3 ma jawny CsvHeader
        Assert.Equal("Title;Tickets;Revenue", Header(typeof(Training.Workshop.M4.S01Rename.Step3.SalesReport.Line)));
    }

    [Fact]
    public void ConfigurationStillNamesTheOldMethod()
    {
        var report = typeof(Training.Workshop.M4.S01Rename.Step3.SalesReport);
        Type[] parameters = [typeof(IReadOnlyList<Training.Workshop.M4.S01Rename.Sale>), typeof(bool)];
        Assert.NotNull(report.GetMethod("Calc2", parameters));
        Assert.NotNull(report.GetMethod("RevenueCsv", parameters));
        // refleksja szuka nazwy z tekstu; bez delegatu Calc2 zadanie przestaje działać
        Assert.Null(report.GetMethod("Revenue", parameters));
    }

    private static string Header(Type line)
    {
        return string.Join(";", line.GetProperties().Select(property => property.Name));
    }
}
