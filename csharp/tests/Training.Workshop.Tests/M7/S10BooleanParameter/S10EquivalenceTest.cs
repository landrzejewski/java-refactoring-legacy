using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M7.S10BooleanParameter;

/// <summary>
/// Równoważność obserwowana przez klientów (wszystkie warianty) oraz przez stare API
/// (dopóki istnieje, czyli start..step3). Wejście: tytuł, format, liczba miejsc, okulary klienta.
/// </summary>
public sealed class S10EquivalenceTest
{
    public sealed record Sale(string Title, string Format, int Seats, bool FlagA, bool FlagB);

    private static readonly Scene<Sale, string> Clients = Support.Scene.Variants<Sale, string>()
        .Variant("start", s =>
        {
            var service = new Training.Workshop.M7.S10BooleanParameter.Start.TicketService();
            return new Training.Workshop.M7.S10BooleanParameter.Start.MobileApp(service)
                    .Buy(s.Title, s.Format, s.Seats)
                + " | " + new Training.Workshop.M7.S10BooleanParameter.Start.BoxOfficeTerminal(service)
                    .Sell(s.Title, s.Format, s.Seats, s.FlagA);
        })
        .Variant("step1", s =>
        {
            var service = new Training.Workshop.M7.S10BooleanParameter.Step1.TicketService();
            return new Training.Workshop.M7.S10BooleanParameter.Step1.MobileApp(service)
                    .Buy(s.Title, s.Format, s.Seats)
                + " | " + new Training.Workshop.M7.S10BooleanParameter.Step1.BoxOfficeTerminal(service)
                    .Sell(s.Title, s.Format, s.Seats, s.FlagA);
        })
        .Variant("step2", s =>
        {
            var service = new Training.Workshop.M7.S10BooleanParameter.Step2.TicketService();
            return new Training.Workshop.M7.S10BooleanParameter.Step2.MobileApp(service)
                    .Buy(s.Title, s.Format, s.Seats)
                + " | " + new Training.Workshop.M7.S10BooleanParameter.Step2.BoxOfficeTerminal(service)
                    .Sell(s.Title, s.Format, s.Seats, s.FlagA);
        })
        .Variant("step3", s =>
        {
            var service = new Training.Workshop.M7.S10BooleanParameter.Step3.TicketService();
            return new Training.Workshop.M7.S10BooleanParameter.Step3.MobileApp(service)
                    .Buy(s.Title, s.Format, s.Seats)
                + " | " + new Training.Workshop.M7.S10BooleanParameter.Step3.BoxOfficeTerminal(service)
                    .Sell(s.Title, s.Format, s.Seats, s.FlagA);
        })
        .Variant("step4", s =>
        {
            var service = new Training.Workshop.M7.S10BooleanParameter.Step4.TicketService();
            return new Training.Workshop.M7.S10BooleanParameter.Step4.MobileApp(service)
                    .Buy(s.Title, s.Format, s.Seats)
                + " | " + new Training.Workshop.M7.S10BooleanParameter.Step4.BoxOfficeTerminal(service)
                    .Sell(s.Title, s.Format, s.Seats, s.FlagA);
        })
        .Expect("IMAX x2", new Sale("Diuna", "IMAX", 2, false, false),
            "Diuna IMAX x2 online: 84.00 | Diuna IMAX x2 kasa: 80.00")
        .Expect("3D x2, klient w kasie ma okulary", new Sale("Kraina Lodu", "3D", 2, true, false),
            "Kraina Lodu 3D x2 online: 74.00 | Kraina Lodu 3D x2 kasa: 64.00")
        .Expect("3D x1, klient w kasie bez okularow", new Sale("Kraina Lodu", "3D", 1, false, false),
            "Kraina Lodu 3D x1 online: 37.00 | Kraina Lodu 3D x1 kasa: 35.00");

    private static readonly Scene<Sale, string> OldApi = OldApiScene();

    public static TheoryData<string> ClientCases => Clients.Tests();

    public static TheoryData<string> OldApiCases => OldApi.Tests();

    [Theory]
    [MemberData(nameof(ClientCases))]
    public void ClientsSeeTheSamePrices(string test) => Clients.Run(test);

    [Theory]
    [MemberData(nameof(OldApiCases))]
    public void OldFlagApiStillWorksDuringMigration(string test) => OldApi.Run(test);

#pragma warning disable CS0618 // odpowiednik @SuppressWarnings("deprecation"): celowo wołamy stare API
    private static Scene<Sale, string> OldApiScene()
    {
        var start = new Training.Workshop.M7.S10BooleanParameter.Start.TicketService();
        var step1 = new Training.Workshop.M7.S10BooleanParameter.Step1.TicketService();
        var step2 = new Training.Workshop.M7.S10BooleanParameter.Step2.TicketService();
        var step3 = new Training.Workshop.M7.S10BooleanParameter.Step3.TicketService();
        var scene = Support.Scene.Variants<Sale, string>()
            .Variant("start", s => start.Book(s.Title, s.Format, s.Seats, s.FlagA, s.FlagB))
            .Variant("step1", s => step1.Book(s.Title, s.Format, s.Seats, s.FlagA, s.FlagB))
            .Variant("step2", s => step2.Book(s.Title, s.Format, s.Seats, s.FlagA, s.FlagB))
            .Variant("step3", s => step3.Book(s.Title, s.Format, s.Seats, s.FlagA, s.FlagB));
        foreach (var online in new[] { true, false })
        {
            foreach (var ownGlasses in new[] { true, false })
            {
                var total = 64 + (ownGlasses ? 0 : 6) + (online ? 4 : 0);
                scene.Expect("online=" + Show(online) + ", ownGlasses=" + Show(ownGlasses),
                    new Sale("Kraina Lodu", "3D", 2, online, ownGlasses),
                    "Kraina Lodu 3D x2 " + (online ? "online" : "kasa") + ": " + total + ".00");
            }
        }
        return scene;
    }
#pragma warning restore CS0618

    private static string Show(bool value) => value ? "true" : "false";
}
