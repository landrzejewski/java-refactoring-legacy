namespace Training.Workshop.M4.S08MoveField;

/// <summary>Stabilne wejście testu - każdy wariant buduje z niego własne Hall i Screening.</summary>
/// <param name="Hall">nazwa sali</param>
/// <param name="VipFromRow">od którego rzędu miejsca są VIP</param>
/// <param name="Format">legacy kod formatu: 1 = 2D, 2 = 3D, 3 = IMAX</param>
/// <param name="Row">wyceniany rząd</param>
public sealed record SeatQuery(string Hall, int VipFromRow, int Format, int Row);
