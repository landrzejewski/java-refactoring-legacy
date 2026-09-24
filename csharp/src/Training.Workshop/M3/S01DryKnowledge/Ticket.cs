namespace Training.Workshop.M3.S01DryKnowledge;

/// <summary>Stabilny kontrakt sceny - bilet do sprzedaży lub zwrotu.</summary>
/// <param name="Format">2D, 3D albo IMAX</param>
/// <param name="Type">NORMAL, STUDENT, SENIOR albo CHILD</param>
/// <param name="Start">godzina rozpoczęcia seansu (przed 12:00 - seans poranny)</param>
public sealed record Ticket(string Format, string Type, TimeOnly Start);
