namespace Training.Workshop.M7.S07Arrowhead;

/// <summary>Stabilny kontrakt sceny: próba rezerwacji z wynikami wcześniejszych sprawdzeń.</summary>
public sealed record BookingAttempt(string Email, bool ScreeningFound, bool SalesOpen,
    bool CustomerBlocked, int RequestedSeats, int FreeSeats);
