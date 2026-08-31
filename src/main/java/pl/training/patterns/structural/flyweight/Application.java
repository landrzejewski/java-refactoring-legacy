package pl.training.patterns.structural.flyweight;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        var forest = List.of(
                new Tree(1, 2, TreeFactory.getTreeType("Oak", "green")),
                new Tree(5, 3, TreeFactory.getTreeType("Pine", "dark green")),
                new Tree(9, 8, TreeFactory.getTreeType("Oak", "green")));
        forest.forEach(Tree::draw);
        System.out.println(TreeFactory.getTreeType("Oak", "green") == TreeFactory.getTreeType("Oak", "green"));
    }

}
