package pl.training.examples.behavioral.mediator;

/**
 * Współpracownik (colleague): zna tylko mediatora, nie innych uczestników.
 */
public class Participant {

    private final String name;
    private final ChatMediator mediator;

    public Participant(String name, ChatMediator mediator) {
        this.name = name;
        this.mediator = mediator;
        mediator.join(this);
    }

    public String getName() {
        return name;
    }

    public void send(String text) {
        System.out.println(name + " sends: " + text);
        mediator.broadcast(this, text);
    }

    public void receive(String from, String text) {
        System.out.println(name + " received from " + from + ": " + text);
    }

}
