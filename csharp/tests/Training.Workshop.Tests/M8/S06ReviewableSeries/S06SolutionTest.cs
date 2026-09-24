using Training.Workshop.M8.S06ReviewableSeries;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M8.S06ReviewableSeries;

/// <summary>
/// Commit 3 zmienia zachowanie - test pokazuje nową regułę i to, że zmienia ona WYŁĄCZNIE
/// bilety NORMAL we wtorek (różnica zachowania między commitem 2 a 3 na siatce przypadków).
/// </summary>
public sealed class S06SolutionTest
{
    private readonly Func<TicketQuery, Money> _before =
        new Training.Workshop.M8.S06ReviewableSeries.Step2.PriceList().Price;
    private readonly Func<TicketQuery, Money> _after =
        new Training.Workshop.M8.S06ReviewableSeries.Step3.PriceList().Price;

    [Fact]
    public void CheapTuesdayGivesNormalTicketTwentyPercentOff()
    {
        Assert.Equal(Money.Of("20.00"), _after(new TicketQuery("2D", "NORMAL", S06EquivalenceTest.TuesdayEvening, 5)));
        // IMAX: 40 - 20% = 32, rano -5, VIP +10
        Assert.Equal(Money.Of("37.00"), _after(new TicketQuery("IMAX", "NORMAL", S06EquivalenceTest.TuesdayMorning, 12)));
        Assert.Equal(Money.Of("25.00"), _after(new TicketQuery("2D", "NORMAL", S06EquivalenceTest.MondayEvening, 5)));
        Assert.Equal(Money.Of("18.75"), _after(new TicketQuery("2D", "STUDENT", S06EquivalenceTest.TuesdayEvening, 5)));
    }

    [Fact]
    public void BehaviourChangeIsLimitedToNormalTicketsOnTuesday()
    {
        var changed = new List<TicketQuery>();
        int checkedCount = 0;
        foreach (string format in new[] { "2D", "3D", "IMAX" })
        {
            foreach (string type in new[] { "NORMAL", "STUDENT", "SENIOR", "CHILD" })
            {
                for (int day = 9; day <= 15; day++)
                {
                    foreach (int hour in new[] { 10, 18 })
                    {
                        foreach (int row in new[] { 1, 10 })
                        {
                            var query = new TicketQuery(format, type, new DateTime(2026, 3, day, hour, 0, 0), row);
                            checkedCount++;
                            if (!_before(query).Equals(_after(query)))
                            {
                                changed.Add(query);
                            }
                        }
                    }
                }
            }
        }
        Assert.Equal(336, checkedCount);
        Assert.Equal(3 * 2 * 2, changed.Count); // 3 formaty x 2 pory x 2 rzędy
        Assert.All(changed, q => Assert.True(q.Type.Equals("NORMAL") && q.Start.DayOfWeek == DayOfWeek.Tuesday));
    }
}
