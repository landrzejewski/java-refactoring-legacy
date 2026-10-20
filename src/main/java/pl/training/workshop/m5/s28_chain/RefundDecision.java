package pl.training.workshop.m5.s28_chain;

/** Stabilny kontrakt sceny: decyzja o zwrocie - kto zatwierdził albo dlaczego odrzucono. */
public record RefundDecision(boolean approved, String approver, String reason) {
    public static RefundDecision approvedBy(String approver) {
        return new RefundDecision(true, approver, null);
    }

    public static RefundDecision rejected(String reason) {
        return new RefundDecision(false, null, reason);
    }

    @Override
    public String toString() {
        return approved ? "APPROVED " + approver : "REJECTED " + reason;
    }
}
