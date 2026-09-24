namespace Training.Workshop.M8.S06ReviewableSeries;

/// <summary>Stabilny kontrakt sceny: pytanie o cenę jednego biletu.</summary>
/// <param name="Format">2D, 3D albo IMAX</param>
/// <param name="Type">NORMAL, STUDENT, SENIOR, CHILD</param>
/// <param name="Row">rząd (10 i dalej to VIP)</param>
public sealed record TicketQuery(string Format, string Type, DateTime Start, int Row);
