namespace Training.Workshop.M2.S09CleanArchitecture.Step2;

/// <summary>
/// Krok 2: port wyjściowy zdefiniowany przez potrzebę przypadku użycia.
/// Zwraca identyfikator; gdy nie da się zapisać - <see cref="InvalidOperationException"/>.
/// </summary>
public interface IReservationStore
{
    string Save(NewReservation reservation);
}
