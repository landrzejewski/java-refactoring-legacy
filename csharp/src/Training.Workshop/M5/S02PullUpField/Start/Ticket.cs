namespace Training.Workshop.M5.S02PullUpField.Start;

/// <summary>
/// Start: baza bez stanu. Każda podklasa trzyma miejsce na sali po swojemu:
/// inna nazwa pola, inny cykl życia (setter kontra konstruktor), inna normalizacja.
/// </summary>
public abstract class Ticket
{
    public abstract string Describe();
}
