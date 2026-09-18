namespace Training.Module6.Polymorphism.After;

public sealed record ApprovalStep : DeploymentStep
{
    public ApprovalStep(string? approver)
    {
        if (string.IsNullOrWhiteSpace(approver))
        {
            throw new ArgumentException("approver must not be blank");
        }
        Approver = approver;
    }

    public string Approver { get; }

    public override string Execute() => "approved-by:" + Approver;
}
