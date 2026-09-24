using Training.Workshop.M6.S16TemplateMethod;
using Training.Workshop.M6.S16TemplateMethod.Step2;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S16TemplateMethod;

/// <summary>Szkielet jest chroniony (niewirtualny - odpowiednik final), a nowy format to tylko trzy metody.</summary>
public sealed class S16SolutionTest
{
    [Fact]
    public void TemplateMethodIsFinal()
    {
        var render = typeof(SalesReport).GetMethod("Render", [typeof(IReadOnlyList<Sale>)])!;
        Assert.False(render.IsVirtual);
    }

    [Fact]
    public void NewFormatReusesTheSkeleton()
    {
        SalesReport markdown = new MarkdownSalesReport();
        Assert.Equal("| godzina | film |\n| 18:00 | Diuna |\nRazem: 40.00\n",
            markdown.Render([new Sale(new TimeOnly(18, 0), "Diuna", 1, Money.Of("40.00"))]));
    }

    private sealed class MarkdownSalesReport : SalesReport
    {
        protected override string Header()
        {
            return "| godzina | film |\n";
        }

        protected override string Row(Sale sale)
        {
            return "| " + sale.Time.ToString("HH:mm", System.Globalization.CultureInfo.InvariantCulture) + " | " + sale.Title + " |\n";
        }

        protected override string Footer(int tickets, Money total)
        {
            return "Razem: " + total + "\n";
        }
    }
}
