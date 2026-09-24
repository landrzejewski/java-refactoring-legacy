namespace Training.Workshop.M4.S11EncapsulateCollection;

/// <summary>Stabilny kontrakt sceny: miejsce (niezmienny rekord - niemodyfikowalna lista wystarczy).</summary>
public sealed record Seat(int Row, int Number)
{
    public override string ToString() => Row + "/" + Number;
}
