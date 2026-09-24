namespace Training.Workshop.M8.S07Adr.Step1.Pricing;

/// <summary>Krok 1: wynik cennika niesie informację o rabacie - zamiast efektu ubocznego mamy daną.</summary>
public sealed record Quote(double Total, bool GroupDiscount);
