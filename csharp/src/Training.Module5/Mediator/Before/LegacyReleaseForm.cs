namespace Training.Module5.Mediator.Before;

/// <summary>
/// Formularz, w którym komponenty są połączone każdy z każdym (siatka zależności).
/// </summary>
public sealed class LegacyReleaseForm
{
    private readonly StatusLabel _statusLabel = new();

    public LegacyReleaseForm()
    {
        VersionField.Connect(ApproverPicker, PublishButton, _statusLabel);
        ApproverPicker.Connect(VersionField, PublishButton, _statusLabel);
        PublishButton.Connect(VersionField, ApproverPicker, _statusLabel);
    }

    public VersionField VersionField { get; } = new();

    public ApproverPicker ApproverPicker { get; } = new();

    public PublishButton PublishButton { get; } = new();

    public FormState Snapshot() =>
        new(
            VersionField.Text,
            VersionField.Editable,
            ApproverPicker.Enabled,
            ApproverPicker.Selection,
            PublishButton.Enabled,
            _statusLabel.Text);
}
