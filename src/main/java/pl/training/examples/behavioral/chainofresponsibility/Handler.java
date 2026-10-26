package pl.training.examples.behavioral.chainofresponsibility;

public abstract class Handler {

    protected Handler nextHandler;

    public abstract void handleRequest(String request);

}
