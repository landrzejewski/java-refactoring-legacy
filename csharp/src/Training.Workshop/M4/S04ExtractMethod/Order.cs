namespace Training.Workshop.M4.S04ExtractMethod;

/// <summary>Stabilny kontrakt sceny - wspólny dla Start i wszystkich kroków.</summary>
/// <param name="Title">tytuł filmu</param>
/// <param name="Format">2D, 3D albo IMAX</param>
/// <param name="Start">godzina seansu</param>
/// <param name="Rows">numery rzędów kupionych miejsc (rząd 10 i dalej to VIP)</param>
public sealed record Order(string Title, string Format, TimeOnly Start, IReadOnlyList<int> Rows);
