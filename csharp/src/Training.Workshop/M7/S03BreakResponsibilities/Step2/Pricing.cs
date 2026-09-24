namespace Training.Workshop.M7.S03BreakResponsibilities.Step2;

/// <summary>Krok 2: wynik wyceny - suma i liczba miejsc VIP (potrzebna w powiadomieniu).</summary>
internal sealed record Pricing(decimal Total, int VipSeats);
