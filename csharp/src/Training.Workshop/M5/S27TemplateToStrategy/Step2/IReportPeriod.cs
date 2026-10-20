namespace Training.Workshop.M5.S27TemplateToStrategy.Step2;

/// <summary>Krok 2: Strategy dla osi "okres" - dwa haki okresu wyjęte z hierarchii.</summary>
public interface IReportPeriod
{
    bool Includes(DateOnly day, DateOnly saleDay);

    string Label(DateOnly day);
}
