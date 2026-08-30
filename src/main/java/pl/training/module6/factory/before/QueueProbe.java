package pl.training.module6.factory.before;

public record QueueProbe(String queueName) implements DeploymentProbe {
    public QueueProbe {
        if (queueName == null || queueName.isBlank()) {
            throw new IllegalArgumentException("queueName must not be blank");
        }
    }

    @Override
    public String check() {
        return "queue-ok:" + queueName;
    }
}
