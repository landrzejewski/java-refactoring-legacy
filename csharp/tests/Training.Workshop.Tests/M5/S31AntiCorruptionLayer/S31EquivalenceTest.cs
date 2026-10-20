using System.Globalization;
using Training.Workshop.M5.S31AntiCorruptionLayer;
using Training.Workshop.Shared;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S31AntiCorruptionLayer;

/// <summary>
/// Rozliczenia i dopuszczenie do emisji na trzech rekordach dystrybutora: udział vs minimalna
/// gwarancja, zaokrąglenie, granice okresu licencji (włącznie), "45%" i " 40 %", licencja
/// zawieszona i nieznany film.
/// </summary>
public sealed class S31EquivalenceTest
{
    private static readonly DistributorFeed Feed = new([
        new DistributorRecord("DIU", "DIUNA", "A", "20261001", "20261031", "50000", "45%"),
        new DistributorRecord("KRL", "KRAINA LODU", "A", "20261015", "20261115", "30000", " 40 %"),
        new DistributorRecord("AMA", "AMATOR", "S", "20260101", "20261231", "10000", "50%")]);

    private static readonly Scene<string, string> Scene =
        Support.Scene.Variants<string, string>()
            .Variant("start", line =>
            {
                var service = Training.Workshop.M5.S31AntiCorruptionLayer.Start.SettlementModule.SettlementService(Feed);
                return Run(line, service.Settle, service.CanScreen);
            })
            .Variant("step1", line =>
            {
                var service = Training.Workshop.M5.S31AntiCorruptionLayer.Step1.SettlementModule.SettlementService(Feed);
                return Run(line, service.Settle, service.CanScreen);
            })
            .Variant("step2", line =>
            {
                var service = Training.Workshop.M5.S31AntiCorruptionLayer.Step2.SettlementModule.SettlementService(Feed);
                return Run(line, service.Settle, service.CanScreen);
            })
            .Variant("step3", line =>
            {
                var service = Training.Workshop.M5.S31AntiCorruptionLayer.Step3.SettlementModule.SettlementService(Feed);
                return Run(line, service.Settle, service.CanScreen);
            })
            .Expect("udział powyżej minimum", "settle DIU 2026-10-20 2000.00",
                "ROZLICZENIE Diuna 2026-10-20: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00")
            .Expect("minimalna gwarancja", "settle DIU 2026-10-20 600.00",
                "ROZLICZENIE Diuna 2026-10-20: przychod 600.00, udzial 45%, minimum 500.00, dla dystrybutora 500.00")
            .Expect("udział ze spacjami i zaokrąglenie", "settle KRL 2026-10-20 1234.57",
                "ROZLICZENIE Kraina lodu 2026-10-20: przychod 1234.57, udzial 40%, minimum 300.00, "
                + "dla dystrybutora 493.83")
            .Expect("pierwszy dzień licencji", "settle DIU 2026-10-01 2000.00",
                "ROZLICZENIE Diuna 2026-10-01: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00")
            .Expect("ostatni dzień licencji", "settle DIU 2026-10-31 2000.00",
                "ROZLICZENIE Diuna 2026-10-31: przychod 2000.00, udzial 45%, minimum 500.00, dla dystrybutora 900.00")
            .Expect("dzień po licencji", "settle DIU 2026-11-01 2000.00", "LICENCJA NIEWAZNA DIU w dniu 2026-11-01")
            .Expect("dzień przed licencją", "settle DIU 2026-09-30 2000.00", "LICENCJA NIEWAZNA DIU w dniu 2026-09-30")
            .Expect("licencja zawieszona", "settle AMA 2026-10-20 100.00", "LICENCJA ZAWIESZONA AMA")
            .Expect("nieznany film", "settle XYZ 2026-10-20 100.00", "BRAK LICENCJI XYZ")
            .Expect("emisja w okresie", "can DIU 2026-10-20", "true")
            .Expect("emisja po okresie", "can DIU 2026-11-01", "false")
            .Expect("emisja przed okresem", "can KRL 2026-10-14", "false")
            .Expect("emisja w ostatnim dniu", "can KRL 2026-11-15", "true")
            .Expect("emisja zawieszonej", "can AMA 2026-10-20", "false")
            .Expect("emisja nieznanego", "can XYZ 2026-10-20", "false");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSettlesTheSame(string test) => Scene.Run(test);

    /// <summary>Wiersz: "settle KOD DZIEN PRZYCHOD" albo "can KOD DZIEN".</summary>
    private static string Run(string line, Func<string, DateOnly, Money, string> settle,
        Func<string, DateOnly, bool> canScreen)
    {
        var parts = line.Split(' ');
        var day = DateOnly.ParseExact(parts[2], "yyyy-MM-dd", CultureInfo.InvariantCulture);
        return parts[0] == "can"
            ? (canScreen(parts[1], day) ? "true" : "false")
            : settle(parts[1], day, Money.Of(parts[3]));
    }
}
