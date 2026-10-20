namespace Training.Module4.Structural.Bridge;

public class TextMessage : Message
{
    public TextMessage(IMessageSender sender) : base(sender)
    {
    }

    public override void Send(string recipient, string text)
    {
        sender.Send(recipient, text);
    }
}
