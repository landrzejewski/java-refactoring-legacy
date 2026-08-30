package pl.training.module6.extractcomposite.after;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class PlanNodes {
    private PlanNodes() {
    }

    public interface PlanNode {
        long totalMinutes();
    }

    public record TaskNode(long minutes) implements PlanNode {
        public TaskNode {
            if (minutes < 0) {
                throw new IllegalArgumentException("minutes must not be negative");
            }
        }

        @Override
        public long totalMinutes() {
            return minutes;
        }
    }

    public abstract static class CompositePlanNode implements PlanNode {
        private final List<PlanNode> children = new ArrayList<>();

        public final void add(PlanNode child) {
            children.add(Objects.requireNonNull(child, "child"));
        }

        public final List<PlanNode> children() {
            return List.copyOf(children);
        }

        @Override
        public final long totalMinutes() {
            long total = 0;
            for (PlanNode child : children) {
                total = Math.addExact(total, child.totalMinutes());
            }
            return total;
        }
    }

    public static final class ReleaseGroup extends CompositePlanNode {
    }

    public static final class RollbackGroup extends CompositePlanNode {
    }
}
