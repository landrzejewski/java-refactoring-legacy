using Training.Workshop.Shared;

namespace Training.Workshop.M5.S27TemplateToStrategy.Step1;

/// <summary>Krok 1: Strategy dla osi "format" - trzy haki formatu wyjęte z hierarchii.</summary>
public interface IReportFormat
{
    string Header(string label);

    string Row(string title, int tickets, Money revenue);

    string Footer(int tickets, Money revenue);
}
