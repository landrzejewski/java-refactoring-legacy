package pl.training.examples.behavioral.mediator;

public class Application {

    public static void main(String[] args) {
        var room = new ChatRoom();
        var alice = new Participant("Alice", room);
        var bob = new Participant("Bob", room);
        new Participant("Carol", room);
        //------------------------------------------------
        alice.send("Hello everyone");
        bob.send("Hi Alice");
    }

}
