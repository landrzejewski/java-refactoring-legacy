package pl.training.module6.polymorphism.after;

public record ScriptStep(String command) implements DeploymentStep {
    public ScriptStep {
        if (command == null || command.isBlank()) {
            throw new IllegalArgumentException("command must not be blank");
        }
    }

    @Override
    public String execute() {
        return "executed:" + command;
    }
}
