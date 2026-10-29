package pl.training.workshop.exercises.e06_movieinfo;

/** Stabilny kontrakt sceny: kto pyta o film. Dane o filmach ogląda tylko personel kina. */
public record User(String name, boolean staff) {
    public static User staff(String name) {
        return new User(name, true);
    }

    public static User guest(String name) {
        return new User(name, false);
    }
}
