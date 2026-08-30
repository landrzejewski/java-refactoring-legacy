package pl.training.patterns.behavioral.visitor;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected final String name;
    protected List<Node> children = new ArrayList<>();

    public void addChild(Node node) {
        children.add(node);
    }

    public void accept(Visitor visitor) {
        children.forEach(node -> node.accept(visitor));
    }

    public Node(final String name) {
        this.name = name;
    }

    public void setChildren(final List<Node> children) {
        this.children = children;
    }
}
