namespace Training.Workshop.M4.S07MoveMethod.Start;

/// <summary>Seans - na razie sam worek danych; zachowanie o seansie mieszka w BookingPrinter.</summary>
/// <param name="Title">tytuł filmu</param>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Start">początek seansu</param>
/// <param name="Hall">numer sali</param>
/// <param name="FreeSeats">numery wolnych miejsc (kolejność ze starego systemu, nie zawsze rosnąca)</param>
public sealed record Screening(string Title, int Format, DateTime Start, int Hall,
    IReadOnlyList<int> FreeSeats);
