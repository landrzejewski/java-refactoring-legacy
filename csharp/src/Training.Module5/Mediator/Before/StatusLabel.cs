namespace Training.Module5.Mediator.Before;

public sealed class StatusLabel
{
    private string _text = "enter version";

    public string Text => _text;

    internal void SetText(string text) => _text = text;
}
