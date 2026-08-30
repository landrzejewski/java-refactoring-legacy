package pl.training.module6.extractcomposite.before;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class LegacyPlanNodes {
    private LegacyPlanNodes() {
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

    public static final class ReleaseGroup implements PlanNode {
        private final List<PlanNode> children = new ArrayList<>();

        public void add(PlanNode child) {
            children.add(Objects.requireNonNull(child, "child"));
        }

        public List<PlanNode> children() {
            return List.copyOf(children);
        }

        @Override
        public long totalMinutes() {
            long total = 0;
            for (PlanNode child : children) {
                total = Math.addExact(total, child.totalMinutes());
            }
            return total;
        }
    }

    public static final class RollbackGroup implements PlanNode {
        private final List<PlanNode> children = new ArrayList<>();

        public void add(PlanNode child) {
            children.add(Objects.requireNonNull(child, "child"));
        }

        public List<PlanNode> children() {
            return List.copyOf(children);
        }

        @Override
        public long totalMinutes() {
            long total = 0;
            for (PlanNode child : children) {
                total = Math.addExact(total, child.totalMinutes());
            }
            return total;
        }
    }
}
