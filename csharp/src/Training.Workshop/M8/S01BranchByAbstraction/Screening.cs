namespace Training.Workshop.M8.S01BranchByAbstraction;

/// <summary>Stabilny kontrakt sceny: seans w kodach legacy.</summary>
/// <param name="Format">1 = 2D, 2 = 3D, 3 = IMAX (jak w CinemaManager)</param>
/// <param name="VipFromRow">pierwszy rząd VIP w sali</param>
public sealed record Screening(string Title, int Format, DateTime Start, int VipFromRow);
