namespace Training.Module5.Mediator.Before;

public sealed class PublishButton
{
    private bool _enabled;
    private VersionField? _versionField;
    private ApproverPicker? _picker;
    private StatusLabel? _label;

    internal void Connect(VersionField versionField, ApproverPicker picker, StatusLabel label)
    {
        _versionField = versionField;
        _picker = picker;
        _label = label;
    }

    public void Click()
    {
        if (!_enabled)
        {
            return;
        }
        _label!.SetText("published " + _versionField!.Text);
        _versionField.Lock();
        _picker!.SetEnabled(false);
        _enabled = false;
    }

    public bool Enabled => _enabled;

    internal void SetEnabled(bool enabled) => _enabled = enabled;
}
