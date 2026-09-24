using Training.Workshop.Shared;

namespace Training.Workshop.M5.S13Sealed.Start;

/// <summary>
/// Start: otwarta hierarchia - konstruktor jest protected, więc każdy (także w innym assembly)
/// może dopisać wariant, a kalkulator o tym nie wie.
/// </summary>
public abstract record Ticket(Money BasePrice);
