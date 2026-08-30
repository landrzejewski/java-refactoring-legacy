package pl.training.module6.composite.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class PlanBuilder {
    private final String name;
    private final List<PlanComponent> components = new ArrayList<>();
    private boolean built;

    private PlanBuilder(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public static PlanBuilder group(String name) {
        return new PlanBuilder(name);
    }

    public PlanBuilder task(String name, long minutes) {
        ensureOpen();
        components.add(new DeploymentTask(name, minutes));
        return this;
    }

    public PlanBuilder group(String name, Consumer<PlanBuilder> definition) {
        ensureOpen();
        PlanBuilder child = group(name);
        Objects.requireNonNull(definition, "definition").accept(child);
        DeploymentGroup builtChild = child.build();
        ensureOpen();
        components.add(builtChild);
        return this;
    }

    public PlanBuilder add(PlanComponent component) {
        ensureOpen();
        components.add(Objects.requireNonNull(component, "component"));
        return this;
    }

    public DeploymentGroup build() {
        ensureOpen();
        built = true;
        return new DeploymentGroup(name, components);
    }

    private void ensureOpen() {
        if (built) {
            throw new IllegalStateException("builder has already been used");
        }
    }
}
