namespace Training.Patterns.Behavioral.ChainOfResponsibility;

public class Validator : Handler
{
    public Validator(Handler nextHandler)
    {
        this.nextHandler = nextHandler;
    }

    public override void HandleRequest(string request)
    {
        if (request.Length < 3)
        {
            return;
        }
        nextHandler!.HandleRequest(request);
    }
}
