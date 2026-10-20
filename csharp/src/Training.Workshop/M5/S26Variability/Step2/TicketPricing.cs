using Training.Workshop.Shared;

namespace Training.Workshop.M5.S26Variability.Step2;

/// <summary>
/// Krok 2: Strategy (bez zmian) - wymienny algorytm ceny jednego biletu (delegat). Zna tylko cenę bazową formatu:
/// nic o rezerwacji, jej statusie ani o operacji kasjera.
/// </summary>
public delegate Money TicketPricing(Money formatBase);
