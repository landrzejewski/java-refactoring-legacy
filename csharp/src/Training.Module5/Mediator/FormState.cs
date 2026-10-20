namespace Training.Module5.Mediator;

/// <summary>
/// Migawka formularza wydania - wspólna dla wersji przed i po refaktoryzacji.
/// </summary>
public sealed record FormState(
    string Version,
    bool VersionEditable,
    bool ApproverEnabled,
    string Approver,
    bool PublishEnabled,
    string Status);
