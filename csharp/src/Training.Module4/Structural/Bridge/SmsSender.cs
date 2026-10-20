namespace Training.Module4.Structural.Bridge;

public class SmsSender : IMessageSender
{
    public void Send(string recipient, string content)
    {
        Console.WriteLine("SMS to " + recipient + ": " + content);
    }
}
