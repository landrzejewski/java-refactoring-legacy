namespace Training.Workshop.M5.S27TemplateToStrategy.Step3;

/// <summary>Krok 3: Strategy (bez zmian od kroku 2) dla osi "okres" - dwa haki okresu wyjęte z hierarchii.</summary>
public interface IReportPeriod
{
    bool Includes(DateOnly day, DateOnly saleDay);

    string Label(DateOnly day);
}
