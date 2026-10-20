namespace Training.Module4.Structural.Bridge;

public class EmailSender : IMessageSender
{
    public void Send(string recipient, string content)
    {
        Console.WriteLine("Email to " + recipient + ": " + content);
    }
}
