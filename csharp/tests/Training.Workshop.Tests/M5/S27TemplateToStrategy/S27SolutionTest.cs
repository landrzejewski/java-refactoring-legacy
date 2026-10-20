using System.Globalization;
using Training.Workshop.M5.S27TemplateToStrategy;
using Training.Workshop.M5.S27TemplateToStrategy.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S27TemplateToStrategy;

/// <summary>Po kroku 3: jedna zapieczętowana klasa, osie dodają się zamiast mnożyć.</summary>
public sealed class S27SolutionTest
{
    private const string Step3 = "Training.Workshop.M5.S27TemplateToStrategy.Step3.";

    [Fact]
    public void ReportIsOneFinalConcreteClassComposedOfTwoStrategies()
    {
        Assert.True(typeof(ScreeningReport).IsSealed);
        Assert.False(typeof(ScreeningReport).IsAbstract);
        Assert.NotNull(typeof(ScreeningReport).GetConstructor([typeof(IReportPeriod), typeof(IReportFormat)]));
    }

    [Fact]
    public void CombinationSubclassesAreGone()
    {
        foreach (var name in new[] { "DailyCsvReport", "DailyTextReport", "WeeklyCsvReport", "WeeklyTextReport" })
        {
            Assert.Null(typeof(ScreeningReport).Assembly.GetType(Step3 + name));
        }
    }

    [Fact]
    public void NewFormatWorksWithEveryPeriodWithoutNewClasses()
    {
        IReportFormat markdown = new MarkdownFormat();
        IReadOnlyList<ScreeningSale> sales =
        [
            new ScreeningSale(Day("2026-10-19"), "Diuna", 2, Money.Of("80.00")),
            new ScreeningSale(Day("2026-10-20"), "Diuna", 1, Money.Of("40.00")),
        ];
        Assert.Equal("## dzien 2026-10-20\n- Diuna: 1\n**40.00**\n",
            new ScreeningReport(new DailyPeriod(), markdown).Render(Day("2026-10-20"), sales));
        Assert.Equal("## tydzien 2026-10-19..2026-10-25\n- Diuna: 3\n**120.00**\n",
            new ScreeningReport(new WeeklyPeriod(), markdown).Render(Day("2026-10-20"), sales));
    }

    /// <summary>Przed zwinięciem szkielet był jeszcze chronioną metodą szablonową (niewirtualną - odpowiednik final).</summary>
    [Fact]
    public void BeforeTheCollapseTheSkeletonWasStillAProtectedTemplateMethod()
    {
        var render = typeof(Training.Workshop.M5.S27TemplateToStrategy.Step2.ScreeningReport)
            .GetMethod("Render", [typeof(DateOnly), typeof(IReadOnlyList<ScreeningSale>)])!;
        Assert.False(render.IsVirtual);
    }

    private static DateOnly Day(string day) => DateOnly.Parse(day, CultureInfo.InvariantCulture);

    private sealed class MarkdownFormat : IReportFormat
    {
        public string Header(string label) => "## " + label + "\n";

        public string Row(string title, int tickets, Money revenue) => "- " + title + ": " + tickets + "\n";

        public string Footer(int tickets, Money revenue) => "**" + revenue + "**\n";
    }
}
