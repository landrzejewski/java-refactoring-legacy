package pl.training.patterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {
    protected final String name;
    protected List<Node> children = new ArrayList<>();

    public void addChild(Node node) {
        children.add(node);
    }

    public abstract void printInfo();

    public Node(final String name) {
        this.name = name;
    }

    public void setChildren(final List<Node> children) {
        this.children = children;
    }
}
