package pl.training.module6.factory.before;

public record HttpProbe(String endpoint) implements DeploymentProbe {
    public HttpProbe {
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("endpoint must not be blank");
        }
    }

    @Override
    public String check() {
        return "http-ok:" + endpoint;
    }
}
