namespace Training.Module4.Structural.Bridge;

public class UrgentMessage : Message
{
    public UrgentMessage(IMessageSender sender) : base(sender)
    {
    }

    public override void Send(string recipient, string text)
    {
        sender.Send(recipient, "[URGENT] " + text.ToUpperInvariant());
    }
}
