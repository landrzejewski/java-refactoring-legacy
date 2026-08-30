package pl.training.module6.composite.after;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import pl.training.module6.composite.before.LegacyPathPlan;

public final class LegacyPathPlanMapper {
    public DeploymentGroup map(
            String rootName,
            List<LegacyPathPlan.Entry> entries) {
        requireRootName(rootName);
        List<LegacyPathPlan.Entry> source = List.copyOf(
                Objects.requireNonNull(entries, "entries"));
        validateDepthFirstOrder(rootName, source);

        GroupNode root = new GroupNode(rootName);
        for (LegacyPathPlan.Entry entry : source) {
            String[] segments = segmentsBelow(rootName, entry);
            GroupNode parent = root;
            for (int index = 1; index < segments.length - 1; index++) {
                parent = parent.group(segments[index], entry.path());
            }
            parent.task(
                    segments[segments.length - 1],
                    entry.minutes(),
                    entry.path());
        }
        return root.freeze();
    }

    private static void validateDepthFirstOrder(
            String rootName,
            List<LegacyPathPlan.Entry> entries) {
        List<String> previousParents = List.of();
        Set<String> closedGroups = new HashSet<>();
        for (LegacyPathPlan.Entry entry : entries) {
            String[] segments = segmentsBelow(rootName, entry);
            List<String> currentParents = parentPaths(segments);
            int common = commonPrefixLength(previousParents, currentParents);
            closedGroups.addAll(
                    previousParents.subList(common, previousParents.size()));
            for (String parent : currentParents) {
                if (closedGroups.contains(parent)) {
                    throw new IllegalArgumentException(
                            "entries are not in depth-first order: "
                                    + entry.path());
                }
            }
            previousParents = currentParents;
        }
    }

    private static String[] segmentsBelow(
            String rootName,
            LegacyPathPlan.Entry entry) {
        Objects.requireNonNull(entry, "entries must not contain null");
        String[] segments = entry.path().split("/", -1);
        if (!segments[0].equals(rootName)) {
            throw new IllegalArgumentException(
                    "entry is outside root " + rootName + ": " + entry.path());
        }
        for (String segment : segments) {
            if (segment.isBlank()) {
                throw new IllegalArgumentException(
                        "path segment must not be blank: " + entry.path());
            }
        }
        return segments;
    }

    private static List<String> parentPaths(String[] segments) {
        List<String> parents = new ArrayList<>();
        StringBuilder path = new StringBuilder(segments[0]);
        for (int index = 1; index < segments.length - 1; index++) {
            path.append('/').append(segments[index]);
            parents.add(path.toString());
        }
        return List.copyOf(parents);
    }

    private static int commonPrefixLength(
            List<String> first,
            List<String> second) {
        int length = Math.min(first.size(), second.size());
        int index = 0;
        while (index < length && first.get(index).equals(second.get(index))) {
            index++;
        }
        return index;
    }

    private static void requireRootName(String rootName) {
        if (rootName == null || rootName.isBlank() || rootName.contains("/")) {
            throw new IllegalArgumentException(
                    "rootName must be one non-blank path segment");
        }
    }

    private sealed interface Node permits GroupNode, TaskNode {
        String name();

        PlanComponent freeze();
    }

    private static final class GroupNode implements Node {
        private final String name;
        private final List<Node> children = new ArrayList<>();
        private final Map<String, GroupNode> groups = new HashMap<>();
        private final Set<String> taskNames = new HashSet<>();

        private GroupNode(String name) {
            this.name = name;
        }

        private GroupNode group(String childName, String sourcePath) {
            if (taskNames.contains(childName)) {
                throw pathConflict(sourcePath, childName);
            }
            GroupNode existing = groups.get(childName);
            if (existing != null) {
                return existing;
            }
            GroupNode created = new GroupNode(childName);
            groups.put(childName, created);
            children.add(created);
            return created;
        }

        private void task(String taskName, long minutes, String sourcePath) {
            if (groups.containsKey(taskName)) {
                throw pathConflict(sourcePath, taskName);
            }
            taskNames.add(taskName);
            children.add(new TaskNode(taskName, minutes));
        }

        private IllegalArgumentException pathConflict(
                String sourcePath,
                String childName) {
            return new IllegalArgumentException(
                    "path is both a task and a group at "
                            + childName + ": " + sourcePath);
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public DeploymentGroup freeze() {
            return new DeploymentGroup(
                    name, children.stream().map(Node::freeze).toList());
        }
    }

    private record TaskNode(String name, long minutes) implements Node {
        @Override
        public DeploymentTask freeze() {
            return new DeploymentTask(name, minutes);
        }
    }
}
