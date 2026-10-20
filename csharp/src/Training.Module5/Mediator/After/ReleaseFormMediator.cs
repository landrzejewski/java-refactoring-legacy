using System.Text.RegularExpressions;

namespace Training.Module5.Mediator.After;

/// <summary>
/// Mediator formularza wydania: tworzy komponenty i trzyma w jednym miejscu wszystkie
/// reguły ich współpracy. Komponenty znają tylko <see cref="IFormMediator"/>, nie siebie
/// nawzajem (pasywna <see cref="StatusLabel"/> nie zna nikogo).
/// </summary>
public sealed class ReleaseFormMediator : IFormMediator
{
    private readonly StatusLabel _statusLabel = new();

    public ReleaseFormMediator()
    {
        // C# field initializers cannot pass "this", so the components are created here.
        VersionField = new VersionField(this);
        ApproverPicker = new ApproverPicker(this);
        PublishButton = new PublishButton(this);
    }

    public VersionField VersionField { get; }

    public ApproverPicker ApproverPicker { get; }

    public PublishButton PublishButton { get; }

    public void VersionChanged()
    {
        var valid = VersionValid();
        ApproverPicker.SetEnabled(valid);
        if (!valid)
        {
            ApproverPicker.Clear();
        }
        Refresh();
    }

    public void ApproverChosen() => Refresh();

    public void PublishClicked()
    {
        _statusLabel.SetText("published " + VersionField.Text);
        VersionField.Lock();
        ApproverPicker.SetEnabled(false);
        PublishButton.SetEnabled(false);
    }

    public FormState Snapshot() =>
        new(
            VersionField.Text,
            VersionField.Editable,
            ApproverPicker.Enabled,
            ApproverPicker.Selection,
            PublishButton.Enabled,
            _statusLabel.Text);

    private void Refresh()
    {
        var valid = VersionValid();
        PublishButton.SetEnabled(valid && ApproverPicker.HasSelection());
        if (!valid)
        {
            _statusLabel.SetText("enter version");
        }
        else if (!ApproverPicker.HasSelection())
        {
            _statusLabel.SetText("choose approver");
        }
        else
        {
            _statusLabel.SetText("ready to publish");
        }
    }

    private bool VersionValid() => Regex.IsMatch(VersionField.Text, "^[0-9]+\\.[0-9]+\\.[0-9]+\\z");
}
