using Training.Workshop.M5.S30Facade;
using Training.Workshop.Tests.Support;

namespace Training.Workshop.Tests.M5.S30Facade;

/// <summary>
/// Ścieżki, na których obie kopie orkiestracji zgadzają się ze sobą: zakup w obu kanałach,
/// konflikt miejsc między kanałami, odmowa w sklepie, nieznany typ biletu. Odmowa w kasie
/// celowo jest poza tą tabelą - tam Start ma błąd, a krok 2 go naprawia (S30BehaviourChangeTest).
/// </summary>
public sealed class S30EquivalenceTest
{
    private static readonly Scene<IReadOnlyList<string>, string> Scene =
        Support.Scene.Variants<IReadOnlyList<string>, string>()
            .Variant("start", Session(() =>
            {
                var cinema = new Training.Workshop.M5.S30Facade.Start.Cinema();
                return Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell, cinema.State);
            }))
            .Variant("step1", Session(() =>
            {
                var cinema = new Training.Workshop.M5.S30Facade.Step1.Cinema();
                return Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell, cinema.State);
            }))
            .Variant("step2", Session(() =>
            {
                var cinema = new Training.Workshop.M5.S30Facade.Step2.Cinema();
                return Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell, cinema.State);
            }))
            .Variant("step3", Session(() =>
            {
                var cinema = new Training.Workshop.M5.S30Facade.Step3.Cinema();
                return Channels(cinema.OnlineShop.Buy, cinema.BoxOffice.Sell, cinema.State);
            }))
            .Expect("sklep: zakup z opłatą rezerwacyjną", [
                "online S1 A1,A2 N 4111 anna@example.com", "state"], """
                OK 54.00
                miejsca={S1=[A1, A2]} platnosci=[CHARGED 4111 54.00] maile=[anna@example.com: Bilety S1 A1,A2 = 54.00]
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("kasa: zakup bez opłaty", ["desk S1 B1 S 4111 jan@example.com", "state"], """
                OK 18.75
                miejsca={S1=[B1]} platnosci=[CHARGED 4111 18.75] maile=[jan@example.com: Bilety S1 B1 = 18.75]
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("miejsce zajęte przez drugi kanał: nic się nie zmienia", [
                "online S1 A1 N 4111 anna@example.com", "desk S1 A2,A1 C 4111 jan@example.com", "state"], """
                OK 27.00
                ERROR seat taken A1
                miejsca={S1=[A1]} platnosci=[CHARGED 4111 27.00] maile=[anna@example.com: Bilety S1 A1 = 27.00]
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("sklep: odmowa płatności zwalnia miejsca", [
                "online S1 A1,A2 E 40000 anna@example.com", "state", "desk S1 A1 N 4111 jan@example.com"], """
                ERROR payment declined
                miejsca={S1=[]} platnosci=[DECLINED 40000 39.00] maile=[]
                OK 25.00
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("nieznany typ biletu: przed rezerwacją", [
                "online S1 A1 X 4111 anna@example.com", "desk S1 A1 X 4111 jan@example.com", "state"], """
                ERROR unknown ticket type: X
                ERROR unknown ticket type: X
                miejsca={} platnosci=[] maile=[]
                """.TrimEnd().ReplaceLineEndings("\n"))
            .Expect("różne seanse się nie blokują", [
                "online S2 A1 C 4111 anna@example.com", "desk S1 A1 N 4111 jan@example.com", "state"],
                "OK 17.00\nOK 25.00\n"
                + "miejsca={S1=[A1], S2=[A1]} platnosci=[CHARGED 4111 17.00, CHARGED 4111 25.00] "
                + "maile=[anna@example.com: Bilety S2 A1 = 17.00, jan@example.com: Bilety S1 A1 = 25.00]");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSellsTheSameOnTheCorrectPaths(string test) => Scene.Run(test);

    /// <summary>Wiersz: "online|desk SEANS MIEJSCA,PO,PRZECINKU TYP KARTA EMAIL" albo "state".</summary>
    internal static Func<string, string> Channels(Func<TicketOrder, string> online, Func<TicketOrder, string> desk,
        Func<string> state)
    {
        return line =>
        {
            var parts = line.Split(' ');
            if (parts[0] == "state")
            {
                return state();
            }
            var order = new TicketOrder(parts[1], parts[2].Split(','), parts[3], parts[4], parts[5]);
            try
            {
                return (parts[0] == "online" ? online : desk)(order);
            }
            catch (ArgumentException exception)
            {
                return "ERROR " + exception.Message;
            }
        };
    }

    private static Func<IReadOnlyList<string>, string> Session(Func<Func<string, string>> console)
    {
        return lines =>
        {
            var handle = console();
            return string.Join("\n", lines.Select(handle).ToList());
        };
    }
}
