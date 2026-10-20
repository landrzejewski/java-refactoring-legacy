package pl.training.module4.behavioral.mediator;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom implements ChatMediator {

    private final List<Participant> participants = new ArrayList<>();

    @Override
    public void join(Participant participant) {
        participants.add(participant);
    }

    @Override
    public void broadcast(Participant sender, String text) {
        for (var participant : participants) {
            if (participant != sender) {
                participant.receive(sender.getName(), text);
            }
        }
    }

}
