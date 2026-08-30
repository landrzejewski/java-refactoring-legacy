package pl.training.patterns.behavioral.iterator;

import java.util.List;

public class Application {

    public static void main(String[] args) {
        var collection = List.of(1, 2, 3, 4, 5);
        var iterator = collection.iterator();
        //------------------------------------------------------
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

}
