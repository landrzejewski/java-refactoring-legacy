package pl.training.module6.polymorphism.after;

public record ApprovalStep(String approver) implements DeploymentStep {
    public ApprovalStep {
        if (approver == null || approver.isBlank()) {
            throw new IllegalArgumentException("approver must not be blank");
        }
    }

    @Override
    public String execute() {
        return "approved-by:" + approver;
    }
}
