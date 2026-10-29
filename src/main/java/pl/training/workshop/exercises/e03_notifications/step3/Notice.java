package pl.training.workshop.exercises.e03_notifications.step3;

import java.util.Objects;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 3: rodzaj wiadomosci - pierwsza os zmiennosci. Podklasa zna tylko tytul i tresc,
 * a wysylke deleguje do kanalu przekazanego w konstruktorze. Dwa rodzaje x trzy kanaly = 5 klas, nie 6,
 * a kazda nowa pozycja na jednej osi to jedna klasa.
 */
public abstract class Notice {
    private final Channel channel;

    protected Notice(Channel channel) {
        this.channel = Objects.requireNonNull(channel, "channel");
    }

    public final void send(Customer customer) {
        channel.deliver(customer, subject(), body());
    }

    protected abstract String subject();

    protected abstract String body();
}
