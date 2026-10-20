using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S32Mediator;

/// <summary>
/// Scenariusze klikania w ekranie wyboru miejsc: każda akcja użytkownika musi odświeżyć te same
/// komponenty w tej samej kolejności. Wynik to komunikaty przycisku i stan ekranu (Render).
/// </summary>
public sealed class S32EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", Session(() =>
            {
                var screen = new Training.Workshop.M5.S32Mediator.Start.SeatSelectionScreen();
                return new Ui(screen.SeatMap.Click, screen.Basket.Remove, screen.Basket.Clear,
                    screen.PriceSummary.ApplyVoucher, screen.PayButton.Press, screen.Render);
            }))
            .Variant("step1", Session(() =>
            {
                var screen = new Training.Workshop.M5.S32Mediator.Step1.SeatSelectionScreen();
                return new Ui(screen.SeatMap.Click, screen.Basket.Remove, screen.Basket.Clear,
                    screen.PriceSummary.ApplyVoucher, screen.PayButton.Press, screen.Render);
            }))
            .Variant("step2", Session(() =>
            {
                var screen = new Training.Workshop.M5.S32Mediator.Step2.SeatSelectionScreen();
                return new Ui(screen.SeatMap.Click, screen.Basket.Remove, screen.Basket.Clear,
                    screen.PriceSummary.ApplyVoucher, screen.PayButton.Press, screen.Render);
            }))
            .Variant("step3", Session(() =>
            {
                var screen = new Training.Workshop.M5.S32Mediator.Step3.SeatSelectionScreen();
                return new Ui(screen.SeatMap.Click, screen.Basket.Remove, screen.Basket.Clear,
                    screen.PriceSummary.ApplyVoucher, screen.PayButton.Press, screen.Render);
            }))
            .Expect("ekran startowy", ["render", "pay"], """
                wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca
                Nieaktywny: Wybierz miejsca
                """)
            .Expect("wybór miejsc: koszyk w kolejności kliknięć, VIP w rzędzie C", [
                "click C2", "click A1", "render"], """
                wybrane=[A1, C2] sprzedane=[B2] koszyk=[C2, A1] suma=60.00 przycisk=Zaplac 60.00
                """)
            .Expect("drugie kliknięcie odznacza", ["click A1", "click A2", "click A1", "render"], """
                wybrane=[A2] sprzedane=[B2] koszyk=[A2] suma=25.00 przycisk=Zaplac 25.00
                """)
            .Expect("sprzedane i spoza sali są ignorowane", ["click B2", "click Z9", "render"], """
                wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca
                """)
            .Expect("usunięcie z koszyka odznacza na mapie", [
                "click A1", "click C1", "remove A1", "remove A3", "render"], """
                wybrane=[C1] sprzedane=[B2] koszyk=[C1] suma=35.00 przycisk=Zaplac 35.00
                """)
            .Expect("limit 4 miejsc wyłącza przycisk", [
                "click A1", "click A2", "click A3", "click A4", "click C1", "render", "pay",
                "remove C1", "render"], """
                wybrane=[A1, A2, A3, A4, C1] sprzedane=[B2] koszyk=[A1, A2, A3, A4, C1] suma=135.00 przycisk=Maksymalnie 4 miejsca
                Nieaktywny: Maksymalnie 4 miejsca
                wybrane=[A1, A2, A3, A4] sprzedane=[B2] koszyk=[A1, A2, A3, A4] suma=100.00 przycisk=Zaplac 100.00
                """)
            .Expect("voucher przelicza sumę i przycisk", [
                "click C1", "click C2", "voucher KINO10", "render", "voucher XYZ", "render"], """
                wybrane=[C1, C2] sprzedane=[B2] koszyk=[C1, C2] suma=63.00 przycisk=Zaplac 63.00
                wybrane=[C1, C2] sprzedane=[B2] koszyk=[C1, C2] suma=70.00 przycisk=Zaplac 70.00
                """)
            .Expect("płatność sprzedaje miejsca i czyści ekran", [
                "click A1", "click C4", "voucher KINO10", "pay", "render", "click A1", "click A2", "render"], """
                Zaplacono 54.00 za A1,C4
                wybrane=[] sprzedane=[A1, B2, C4] koszyk=[] suma=0.00 przycisk=Wybierz miejsca
                wybrane=[A2] sprzedane=[A1, B2, C4] koszyk=[A2] suma=22.50 przycisk=Zaplac 22.50
                """)
            .Expect("wyczyszczenie koszyka", ["click A1", "click C3", "clear", "render"], """
                wybrane=[] sprzedane=[B2] koszyk=[] suma=0.00 przycisk=Wybierz miejsca
                """);

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepCoordinatesTheScreenTheSame(string test) => Scene.Run(test);

    /// <summary>Akcje użytkownika na komponentach ekranu.</summary>
    private sealed record Ui(
        Action<string> Click,
        Action<string> Remove,
        Action Clear,
        Action<string> Voucher,
        Func<string> Pay,
        Func<string> Render)
    {
        /// <summary>Zwraca komunikat dla "pay" i "render", null dla akcji bez komunikatu.</summary>
        public string? Handle(string line)
        {
            var parts = line.Split(' ');
            switch (parts[0])
            {
                case "click":
                    Click(parts[1]);
                    break;
                case "remove":
                    Remove(parts[1]);
                    break;
                case "clear":
                    Clear();
                    break;
                case "voucher":
                    Voucher(parts[1]);
                    break;
                case "pay":
                    return Pay();
                case "render":
                    return Render();
                default:
                    throw new ArgumentException(line);
            }
            return null;
        }
    }

    private static Func<IReadOnlyList<string>, string> Session(Func<Ui> screen)
    {
        return lines =>
        {
            var ui = screen();
            return string.Join("\n", lines.Select(ui.Handle).OfType<string>().ToList());
        };
    }
}
