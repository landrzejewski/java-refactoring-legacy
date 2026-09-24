namespace Training.Workshop.M6.S08State;

/// <summary>Stabilny kontrakt rezerwacji - wspólny dla Start i kroków, używany przez test tabeli przejść.</summary>
public interface IReservationActions
{
    void Pay();

    void Use();

    void Expire();

    void Cancel();

    Status Status { get; }

    /// <summary>Efekty uboczne w kolejności wystąpienia.</summary>
    IReadOnlyList<string> Effects { get; }
}
