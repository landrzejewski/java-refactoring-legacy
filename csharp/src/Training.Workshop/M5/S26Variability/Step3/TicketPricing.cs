using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step3;

/// <summary>
/// Krok 3: Strategy (bez zmian od kroku 1) - wymienny algorytm ceny jednego biletu (delegat). Zna tylko cenę bazową formatu:
/// nic o rezerwacji, jej statusie ani o operacji kasjera.
/// </summary>
public delegate Money TicketPricing(Money formatBase);
