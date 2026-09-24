using System.Text;

namespace Training.Workshop.Tests.M3.S10Isp;

/// <summary>Ten sam dzień pracy kasy, raportu i tablicy seansów w każdym wariancie.</summary>
public sealed class S10EquivalenceTest
{
    private static readonly Support.Scene<int, string> Scene = Support.Scene.Variants<int, string>()
        .Variant("start", dune =>
        {
            var office = new Training.Workshop.M3.S10Isp.Start.InMemoryBackOffice();
            var board = new Training.Workshop.M3.S10Isp.Start.ScheduleBoard(office);
            var desk = new Training.Workshop.M3.S10Isp.Start.CashDesk(office);
            var report = new Training.Workshop.M3.S10Isp.Start.RevenueReport(office);
            board.Plan("Diuna", new TimeOnly(20, 0));
            board.Plan("Amator", new TimeOnly(18, 0));
            board.Plan("Kraina Lodu", new TimeOnly(10, 0));
            board.Cancel("Kraina Lodu");
            var log = new StringBuilder();
            for (var seat = 1; seat <= dune; seat++)
            {
                log.Append(desk.Sell("Diuna", seat)).Append("; ");
            }
            log.Append(desk.Sell("Amator", 7)).Append("; ");
            if (dune > 0)
            {
                log.Append(desk.Refund("T-1")).Append("; ");
            }
            return log + board.Board() + " | " + report.Summary("Diuna");
        })
        .Variant("step1", dune =>
        {
            var office = new Training.Workshop.M3.S10Isp.Step1.InMemoryBackOffice();
            var board = new Training.Workshop.M3.S10Isp.Step1.ScheduleBoard(office);
            var desk = new Training.Workshop.M3.S10Isp.Step1.CashDesk(office);
            var report = new Training.Workshop.M3.S10Isp.Step1.RevenueReport(office);
            board.Plan("Diuna", new TimeOnly(20, 0));
            board.Plan("Amator", new TimeOnly(18, 0));
            board.Plan("Kraina Lodu", new TimeOnly(10, 0));
            board.Cancel("Kraina Lodu");
            var log = new StringBuilder();
            for (var seat = 1; seat <= dune; seat++)
            {
                log.Append(desk.Sell("Diuna", seat)).Append("; ");
            }
            log.Append(desk.Sell("Amator", 7)).Append("; ");
            if (dune > 0)
            {
                log.Append(desk.Refund("T-1")).Append("; ");
            }
            return log + board.Board() + " | " + report.Summary("Diuna");
        })
        .Variant("step2", dune =>
        {
            var office = new Training.Workshop.M3.S10Isp.Step2.InMemoryBackOffice();
            var board = new Training.Workshop.M3.S10Isp.Step2.ScheduleBoard(office);
            var desk = new Training.Workshop.M3.S10Isp.Step2.CashDesk(office);
            var report = new Training.Workshop.M3.S10Isp.Step2.RevenueReport(office);
            board.Plan("Diuna", new TimeOnly(20, 0));
            board.Plan("Amator", new TimeOnly(18, 0));
            board.Plan("Kraina Lodu", new TimeOnly(10, 0));
            board.Cancel("Kraina Lodu");
            var log = new StringBuilder();
            for (var seat = 1; seat <= dune; seat++)
            {
                log.Append(desk.Sell("Diuna", seat)).Append("; ");
            }
            log.Append(desk.Sell("Amator", 7)).Append("; ");
            if (dune > 0)
            {
                log.Append(desk.Refund("T-1")).Append("; ");
            }
            return log + board.Board() + " | " + report.Summary("Diuna");
        })
        .Expect("trzy bilety na Diune, jeden zwrocony", 3,
            "bilet T-1: Diuna, miejsce 1; bilet T-2: Diuna, miejsce 2; bilet T-3: Diuna, miejsce 3; "
                + "bilet T-4: Amator, miejsce 7; zwrot T-1; "
                + "18:00 Amator, 20:00 Diuna | Diuna: 2 biletow, dzien: 75.00")
        .Expect("bez biletow na Diune", 0,
            "bilet T-1: Amator, miejsce 7; 18:00 Amator, 20:00 Diuna | Diuna: 0 biletow, dzien: 25.00");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void ClientsBehaveTheSame(string test) => Scene.Run(test);
}
