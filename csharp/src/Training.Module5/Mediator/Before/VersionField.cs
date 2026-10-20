using System.Text.RegularExpressions;

namespace Training.Module5.Mediator.Before;

/// <summary>
/// Pole wersji zna trzy inne komponenty i samo wylicza ich stan.
/// </summary>
public sealed class VersionField
{
    private string _text = "";
    private bool _editable = true;
    private ApproverPicker? _picker;
    private PublishButton? _button;
    private StatusLabel? _label;

    internal void Connect(ApproverPicker picker, PublishButton button, StatusLabel label)
    {
        _picker = picker;
        _button = button;
        _label = label;
    }

    public void Type(string value)
    {
        if (!_editable)
        {
            return;
        }
        _text = value;
        var valid = IsValid();
        _picker!.SetEnabled(valid);
        if (!valid)
        {
            _picker.Clear();
        }
        _button!.SetEnabled(valid && _picker.HasSelection());
        if (!valid)
        {
            _label!.SetText("enter version");
        }
        else if (!_picker.HasSelection())
        {
            _label!.SetText("choose approver");
        }
        else
        {
            _label!.SetText("ready to publish");
        }
    }

    public string Text => _text;

    public bool Editable => _editable;

    internal bool IsValid() => Regex.IsMatch(_text, "^[0-9]+\\.[0-9]+\\.[0-9]+\\z");

    internal void Lock() => _editable = false;
}
