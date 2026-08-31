package pl.training.patterns.behavioral.command;

public class Application {

    public static void main(String[] args) {
        var invoker = new Invoker();
        invoker.invoke(new PrintTime());
        invoker.invoke(new ConnectToServer());
    }

}
