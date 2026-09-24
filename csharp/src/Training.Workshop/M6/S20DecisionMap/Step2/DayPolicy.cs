using Training.Workshop.Shared;

namespace Training.Workshop.M6.S20DecisionMap.Step2;

/// <summary>Krok 2 (ścieżka A): korekta ceny za dzień jako wymienny algorytm - Strategy (delegat).</summary>
public delegate Money DayPolicy(Money basePrice);
