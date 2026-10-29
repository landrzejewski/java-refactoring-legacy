package pl.training.workshop.exercises.e10_showtime.start;

/**
 * Start: sterownik sali tylko składa urządzenia i spina je ze sobą ("każde z każdym"), a potem
 * przekazuje polecenie pierwszemu urządzeniu łańcucha. Kolejność reakcji jest rozsiana po
 * Doors, AdPlayer i Projector; tylko procedura awaryjna jest zapisana tutaj.
 */
public final class HallController {
    private final Lights lights;
    private final Sound sound;
    private final Doors doors;
    private final AdPlayer adPlayer;
    private final Projector projector;

    public HallController(DeviceLog log) {
        lights = new Lights(log);
        sound = new Sound(log);
        doors = new Doors(log);
        adPlayer = new AdPlayer(log);
        projector = new Projector(log);
        doors.wire(lights, adPlayer);
        adPlayer.wire(lights, sound, projector);
        projector.wire(sound, lights, doors);
    }

    public void startShow(String title) {
        doors.close(title);
    }

    public void endShow() {
        projector.stop();
    }

    public void emergency() {
        lights.set(100);
        doors.open();
        sound.set(0);
        adPlayer.stop();
        projector.cut();
    }

    // Zdarzenia sprzętu (w teście symulowane poleceniami skryptu).

    public void adTick() {
        adPlayer.tick();
    }

    public void doorObstacle() {
        doors.obstacle();
    }

    public void doorCleared() {
        doors.cleared();
    }

    public void volume(int level) {
        sound.set(level);
    }
}
