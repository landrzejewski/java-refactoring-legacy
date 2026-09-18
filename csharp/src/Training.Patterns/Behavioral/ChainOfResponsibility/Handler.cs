namespace Training.Patterns.Behavioral.ChainOfResponsibility;

public abstract class Handler
{
    protected Handler? nextHandler;

    public abstract void HandleRequest(string request);
}
