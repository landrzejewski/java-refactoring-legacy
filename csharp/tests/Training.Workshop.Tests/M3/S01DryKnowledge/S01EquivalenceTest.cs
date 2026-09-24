using System.Globalization;
using Training.Workshop.M3.S01DryKnowledge;

namespace Training.Workshop.Tests.M3.S01DryKnowledge;

/// <summary>Start i każdy krok: ta sama cena sprzedaży i ta sama kwota zwrotu.</summary>
public sealed class S01EquivalenceTest
{
    public sealed record Case(Ticket Ticket, long HoursBeforeStart);

    private static readonly Support.Scene<Case, string> Scene = Support.Scene.Variants<Case, string>()
        .Variant("start", c =>
        {
            var office = new Training.Workshop.M3.S01DryKnowledge.Start.BoxOffice();
            return Show(office.Sell(c.Ticket)) + " / " + Show(office.Refund(c.Ticket, c.HoursBeforeStart));
        })
        .Variant("step1", c =>
        {
            var office = new Training.Workshop.M3.S01DryKnowledge.Step1.BoxOffice();
            return Show(office.Sell(c.Ticket)) + " / " + Show(office.Refund(c.Ticket, c.HoursBeforeStart));
        })
        .Variant("step2", c =>
        {
            var office = new Training.Workshop.M3.S01DryKnowledge.Step2.BoxOffice();
            return Show(office.Sell(c.Ticket)) + " / " + Show(office.Refund(c.Ticket, c.HoursBeforeStart));
        })
        .Variant("step3", c =>
        {
            var office = new Training.Workshop.M3.S01DryKnowledge.Step3.BoxOffice();
            return Show(office.Sell(c.Ticket)) + " / " + Show(office.Refund(c.Ticket, c.HoursBeforeStart));
        })
        .Variant("step4", c =>
        {
            var office = new Training.Workshop.M3.S01DryKnowledge.Step4.BoxOffice();
            return Show(office.Sell(c.Ticket)) + " / " + Show(office.Refund(c.Ticket, c.HoursBeforeStart));
        })
        .Expect("IMAX normalny, zwrot 48h przed: 100% - 3.00",
            new Case(new Ticket("IMAX", "NORMAL", new TimeOnly(20, 0)), 48), "40.00 / 37.00")
        .Expect("3D student rano, zwrot 10h przed: 50% - 3.00",
            new Case(new Ticket("3D", "STUDENT", new TimeOnly(11, 0)), 10), "19.00 / 6.50")
        .Expect("2D senior, zwrot po starcie: 0, nie ponizej zera",
            new Case(new Ticket("2D", "SENIOR", new TimeOnly(18, 0)), 0), "17.50 / 0.00")
        .Expect("2D dziecko rano, zwrot dokladnie 24h przed",
            new Case(new Ticket("2D", "CHILD", new TimeOnly(10, 0)), 24), "10.00 / 7.00")
        .Expect("3D senior wieczorem, zwrot 1h przed",
            new Case(new Ticket("3D", "SENIOR", new TimeOnly(21, 15)), 1), "22.40 / 8.20");

    public static TheoryData<string> Cases => Scene.Tests();

    [Theory]
    [MemberData(nameof(Cases))]
    public void EveryStepSellsAndRefundsTheSame(string test) => Scene.Run(test);

    private static string Show(decimal amount) => amount.ToString("0.00", CultureInfo.InvariantCulture);
}
