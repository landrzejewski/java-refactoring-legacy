namespace Training.Workshop.M3.S14ReversiblePattern;

/// <summary>Stabilny kontrakt sceny - umowa z dystrybutorem filmu.</summary>
/// <param name="Title">tytuł filmu</param>
/// <param name="Model">PERCENT (procent od przychodu z biletów) albo FESTIVAL (stawka z systemu festiwalu)</param>
public sealed record Deal(string Title, string Model);
