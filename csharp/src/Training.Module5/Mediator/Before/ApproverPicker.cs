namespace Training.Module5.Mediator.Before;

/// <summary>
/// Wybór zatwierdzającego - sprawdza pole wersji i powtarza logikę etykiety.
/// </summary>
public sealed class ApproverPicker
{
    private string _selection = "";
    private bool _enabled;
    private VersionField? _versionField;
    private PublishButton? _button;
    private StatusLabel? _label;

    internal void Connect(VersionField versionField, PublishButton button, StatusLabel label)
    {
        _versionField = versionField;
        _button = button;
        _label = label;
    }

    public void Choose(string approver)
    {
        if (!_enabled)
        {
            return;
        }
        _selection = approver;
        _button!.SetEnabled(_versionField!.IsValid() && HasSelection());
        if (!_versionField.IsValid())
        {
            _label!.SetText("enter version");
        }
        else if (!HasSelection())
        {
            _label!.SetText("choose approver");
        }
        else
        {
            _label!.SetText("ready to publish");
        }
    }

    public string Selection => _selection;

    public bool Enabled => _enabled;

    internal bool HasSelection() => !string.IsNullOrWhiteSpace(_selection);

    internal void SetEnabled(bool enabled) => _enabled = enabled;

    internal void Clear() => _selection = "";
}
