package pl.training.module4.behavioral.command;

public class Application {

    public static void main(String[] args) {
        var invoker = new Invoker();
        invoker.invoke(new PrintTime());
        invoker.invoke(new ConnectToServer());
    }

}
