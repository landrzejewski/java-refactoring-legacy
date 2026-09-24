namespace Training.Workshop.M6.S13ExtractComposite.Step1;

/// <summary>Krok 1: bez zmian - element programu kina (film albo kontener filmów).</summary>
public interface IProgramItem
{
    int Minutes { get; }

    string Describe();
}
