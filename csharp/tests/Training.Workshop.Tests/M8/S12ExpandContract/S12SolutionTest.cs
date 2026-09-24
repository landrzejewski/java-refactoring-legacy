using System.Text.RegularExpressions;
using Training.Workshop.M8.S12ExpandContract;
using Training.Workshop.Tests.Support;
using static Training.Workshop.Tests.M8.S12ExpandContract.S12EquivalenceTest;

namespace Training.Workshop.Tests.M8.S12ExpandContract;

/// <summary>Dane, wycofanie i zamknięcie migracji formatu rezerwacji (expand and contract).</summary>
public sealed class S12SolutionTest
{
    [Fact]
    public void Step1DualWriteKeepsRollbackToTheOldVersionSafe()
    {
        var table = new BookingTable();
        new Training.Workshop.M8.S12ExpandContract.Step1.BookingRepository(table).Save(Anna);
        Assert.Equal("v2|id=B1|email=anna@kino.pl|seats=A5 A10|total=84.00", table.Get("B1")!.Payload);
        var oldVersion = new Training.Workshop.M8.S12ExpandContract.Start.BookingRepository(table);
        // wycofane wydanie czyta dane nowego
        Assert.Equal(Anna, oldVersion.Find("B1"));
    }

    [Fact]
    public void Step2ReadsNewFormatAndFallsBackForOldRows()
    {
        var table = new BookingTable();
        new Training.Workshop.M8.S12ExpandContract.Start.BookingRepository(table).Save(Jan);
        var repository = new Training.Workshop.M8.S12ExpandContract.Step2.BookingRepository(table);
        repository.Save(Anna);
        // stary wiersz - odczyt z fallbackiem
        Assert.Equal(Jan, repository.Find("B2"));
        Assert.Equal(Anna, repository.Find("B1"));
        Assert.Equal(Anna, new Training.Workshop.M8.S12ExpandContract.Start.BookingRepository(table).Find("B1"));
    }

    [Fact]
    public void Step3BackfillIsIdempotentAndPreparesTheContract()
    {
        var table = new BookingTable();
        new Training.Workshop.M8.S12ExpandContract.Start.BookingRepository(table).Save(Jan);
        var contracted = new Training.Workshop.M8.S12ExpandContract.Step4.BookingRepository(table);
        // contract przed backfillem gubi stare wiersze
        Assert.Null(contracted.Find("B2"));

        var repository = new Training.Workshop.M8.S12ExpandContract.Step3.BookingRepository(table);
        Assert.Equal(1, repository.MigrateAll());
        // drugie uruchomienie niczego nie zmienia
        Assert.Equal(0, repository.MigrateAll());
        Assert.Equal(Jan, contracted.Find("B2"));
    }

    [Fact]
    public void Step4ClosesTheRollbackWindow()
    {
        var table = new BookingTable();
        new Training.Workshop.M8.S12ExpandContract.Step4.BookingRepository(table).Save(Anna);
        // po contract stara wersja nie widzi nowych danych - wycofanie kodu już nie wystarczy
        Assert.Null(new Training.Workshop.M8.S12ExpandContract.Start.BookingRepository(table).Find("B1"));
    }

    [Fact]
    public void PayloadFormatIsVersioned()
    {
        Assert.Throws<ArgumentException>(() =>
            Training.Workshop.M8.S12ExpandContract.Step4.BookingPayloadFormat.Read("v3|id=B1"));
    }

    [Fact]
    public void Step4HasNoReferenceToTheOldFormat()
    {
        string step4 = SourceFiles.Workshop("M8", "S12ExpandContract", "Step4");
        Assert.True(Directory.Exists(step4), "brak katalogu sceny " + Path.GetFullPath(step4));
        var oldFormat = new Regex("csv", RegexOptions.IgnoreCase);
        IReadOnlyList<string> offenders = Directory.GetFiles(step4)
            .Where(file => oldFormat.IsMatch(File.ReadAllText(file)))
            .Select(file => Path.GetFileName(file))
            .ToList();
        Assert.Empty(offenders);
        var assembly = typeof(Training.Workshop.M8.S12ExpandContract.Step4.BookingRepository).Assembly;
        Assert.Null(assembly.GetType("Training.Workshop.M8.S12ExpandContract.Step4.CsvBookingFormat"));
    }
}
