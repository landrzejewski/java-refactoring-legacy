package pl.training.patterns.structural.flyweight;

import java.util.HashMap;
import java.util.Map;

public class TreeFactory {

    private static final Map<String, TreeType> TYPES = new HashMap<>();

    private TreeFactory() {
    }

    public static TreeType getTreeType(String name, String color) {
        return TYPES.computeIfAbsent(name + ":" + color, key -> new TreeType(name, color));
    }

}
