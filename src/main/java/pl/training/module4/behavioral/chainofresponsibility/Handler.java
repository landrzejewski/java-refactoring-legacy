package pl.training.module4.behavioral.chainofresponsibility;

public abstract class Handler {

    protected Handler nextHandler;

    public abstract void handleRequest(String request);

}
