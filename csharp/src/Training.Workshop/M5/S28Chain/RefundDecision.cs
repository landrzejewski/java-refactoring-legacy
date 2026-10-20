namespace Training.Workshop.M5.S28Chain;

/// <summary>Stabilny kontrakt sceny: decyzja o zwrocie - kto zatwierdził albo dlaczego odrzucono.</summary>
public sealed record RefundDecision(bool Approved, string? Approver, string? Reason)
{
    public static RefundDecision ApprovedBy(string approver) => new(true, approver, null);

    public static RefundDecision Rejected(string reason) => new(false, null, reason);

    public override string ToString() => Approved ? "APPROVED " + Approver : "REJECTED " + Reason;
}
