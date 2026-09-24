namespace Training.Workshop.M7.S06ParameterObject.Step1;

/// <summary>Krok 1: Parameter Object nazywa pojęcie "termin seansu w sali". Stan przejściowy: bez walidacji.</summary>
public sealed record ScreeningSlot(string ScreeningId, DateOnly Date, int Hall, string Format);
