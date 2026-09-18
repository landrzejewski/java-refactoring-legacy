namespace Training.Module6.Polymorphism.Before;

public sealed record LegacyDeploymentStep
{
    public LegacyDeploymentStep(Kind kind, string? value)
    {
        if (string.IsNullOrWhiteSpace(value))
        {
            var field = kind == Kind.Script ? "command" : "approver";
            throw new ArgumentException(field + " must not be blank");
        }
        Kind = kind;
        Value = value;
    }

    public Kind Kind { get; }

    public string Value { get; }

    public static LegacyDeploymentStep Script(string? command) => new(Kind.Script, command);

    public static LegacyDeploymentStep Approval(string? approver) => new(Kind.Approval, approver);

    public string Execute() => Kind switch
    {
        Kind.Script => "executed:" + Value,
        Kind.Approval => "approved-by:" + Value,
        _ => throw new InvalidOperationException("unknown kind: " + Kind)
    };
}

// Java nests this enum as LegacyDeploymentStep.Kind; C# forbids a nested type
// with the same name as the Kind property, so it lives next to the record.
public enum Kind
{
    Script,
    Approval
}
