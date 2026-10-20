namespace Training.Module4.Behavioral.ChainOfResponsibility;

public abstract class Handler
{
    protected Handler? nextHandler;

    public abstract void HandleRequest(string request);
}
