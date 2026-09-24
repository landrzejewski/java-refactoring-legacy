namespace Training.Workshop.M5.S14Reuse.Step2;

/// <summary>
/// Krok 2: Extract Interface - rola dla raportu. Wspólny KONTRAKT (właściciel i saldo) jest prawdziwy
/// dla obu kont, więc tu podtypowanie jest uczciwe. Wymiany na bilet w nim nie ma.
/// </summary>
public interface IPointsHolder
{
    string Owner { get; }

    int Points { get; }
}
