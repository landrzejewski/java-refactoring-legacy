namespace Training.Module4.Structural.Bridge;

public class Application
{
    public static void Run()
    {
        var email = new EmailSender();
        var sms = new SmsSender();
        IReadOnlyList<Message> messages =
        [
            new TextMessage(email),
            new TextMessage(sms),
            new UrgentMessage(email),
            new UrgentMessage(sms),
        ];
        //------------------------------------------------
        foreach (var message in messages)
        {
            message.Send("anna", "Server restarted");
        }
    }
}
