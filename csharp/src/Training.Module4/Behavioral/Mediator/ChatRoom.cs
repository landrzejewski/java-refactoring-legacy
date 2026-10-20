namespace Training.Module4.Behavioral.Mediator;

public class ChatRoom : IChatMediator
{
    private readonly List<Participant> participants = [];

    public void Join(Participant participant)
    {
        participants.Add(participant);
    }

    public void Broadcast(Participant sender, string text)
    {
        foreach (var participant in participants)
        {
            if (participant != sender)
            {
                participant.Receive(sender.Name, text);
            }
        }
    }
}
