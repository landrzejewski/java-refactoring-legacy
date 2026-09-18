using System.Text;

namespace Training.Module8.Documentation;

public sealed class DecisionRecordMarkdownRenderer
{
    public string Render(DecisionRecord record)
    {
        ArgumentNullException.ThrowIfNull(record);

        var markdown = new StringBuilder();
        markdown.Append("# ")
            .Append(record.Id)
            .Append(": ")
            .Append(record.Title)
            .Append("\n\n");
        Section(markdown, "Status", StatusLabel(record.Status));
        Section(markdown, "Kontekst", record.Context);
        Section(markdown, "Decyzja", record.Decision);

        markdown.Append("## Rozważone opcje\n\n");
        for (int index = 0; index < record.ConsideredOptions.Length; index++)
        {
            DecisionOption option = record.ConsideredOptions[index];
            markdown.Append(index + 1)
                .Append(". **")
                .Append(option.Name)
                .Append("**: ")
                .Append(option.Rationale)
                .Append('\n');
        }
        markdown.Append('\n');

        markdown.Append("## Konsekwencje\n\n");
        foreach (DecisionConsequence consequence in record.Consequences)
        {
            markdown.Append("- **")
                .Append(ConsequenceLabel(consequence.Kind))
                .Append("**: ")
                .Append(consequence.Description)
                .Append('\n');
        }
        markdown.Append('\n');
        Section(markdown, "Metoda weryfikacji", record.VerificationMethod);
        return markdown.ToString();
    }

    private static void Section(
        StringBuilder markdown,
        string heading,
        string content)
    {
        markdown.Append("## ")
            .Append(heading)
            .Append("\n\n")
            .Append(content)
            .Append("\n\n");
    }

    private static string StatusLabel(DecisionStatus status) => status switch
    {
        DecisionStatus.Proposed => "Proponowana",
        DecisionStatus.Accepted => "Zaakceptowana",
        DecisionStatus.Rejected => "Odrzucona",
        DecisionStatus.Deprecated => "Wycofana",
        DecisionStatus.Superseded => "Zastąpiona",
        _ => throw new ArgumentOutOfRangeException(nameof(status), status, null)
    };

    private static string ConsequenceLabel(ConsequenceKind kind) => kind switch
    {
        ConsequenceKind.Positive => "Pozytywna",
        ConsequenceKind.Negative => "Negatywna",
        ConsequenceKind.Neutral => "Neutralna",
        _ => throw new ArgumentOutOfRangeException(nameof(kind), kind, null)
    };
}
