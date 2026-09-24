namespace Training.Workshop.M3.S12CleanArchitecture.Step4.App;

/// <summary>
/// Krok 2: port wyjściowy zdefiniowany przez potrzebę przypadku użycia.
/// Zwraca identyfikator; gdy nie da się zapisać - <see cref="InvalidOperationException"/>.
/// </summary>
public interface IReservationStore
{
    string Save(NewReservation reservation);
}
