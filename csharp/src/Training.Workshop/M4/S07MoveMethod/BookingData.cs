namespace Training.Workshop.M4.S07MoveMethod;

/// <summary>
/// Stabilne wejście testu - z niego każdy wariant buduje własne Screening i Booking
/// (typy te zmieniają się w trakcie sceny, więc mieszkają w przestrzeniach Start/StepN).
/// </summary>
public sealed record BookingData(string Id, string Title, int Format, DateTime Start, int Hall,
    IReadOnlyList<int> FreeSeats, int? Seat);
