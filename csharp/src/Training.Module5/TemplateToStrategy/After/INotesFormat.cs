namespace Training.Module5.TemplateToStrategy.After;

/// <summary>
/// Strategia formatu - dawne kroki <c>Header/FormatEntry/Footer</c> szablonu.
/// </summary>
public interface INotesFormat
{
    string Header(string title, string version);

    string Entry(ChangeEntry entry);

    string Footer();
}
