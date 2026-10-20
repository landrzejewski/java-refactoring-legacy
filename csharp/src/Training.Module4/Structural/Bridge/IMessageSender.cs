namespace Training.Module4.Structural.Bridge;

/// <summary>
/// Implementacja w moście: kanał, którym fizycznie wysyłamy wiadomość.
/// Rozwija się niezależnie od hierarchii <see cref="Message"/>.
/// </summary>
public interface IMessageSender
{
    void Send(string recipient, string content);
}
