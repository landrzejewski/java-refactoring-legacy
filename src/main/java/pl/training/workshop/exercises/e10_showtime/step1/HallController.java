package pl.training.workshop.exercises.e10_showtime.step1;

/**
 * Krok 1: sterownik sali jest jedynym miejscem, które zna wszystkie urządzenia i kolejność ich reakcji.
 * Urządzenia zgłaszają zdarzenia przez {@link HallMediator}, a sterownik decyduje, co dalej.
 * Logika urządzeń (limit głośności, przeszkoda w drzwiach, stan projektora) zostaje w urządzeniach.
 */
public final class HallController implements HallMediator {
    private static final int ADS_BEFORE_FEATURE = 2;

    private final Lights lights;
    private final Sound sound;
    private final Doors doors;
    private final AdPlayer adPlayer;
    private final Projector projector;
    private String pendingTitle;
    private boolean closeWhenCleared;

    public HallController(DeviceLog log) {
        lights = new Lights(log);
        sound = new Sound(log);
        doors = new Doors(log, this);
        adPlayer = new AdPlayer(log, this);
        projector = new Projector(log);
    }

    // Polecenia klientów sali.

    public void startShow(String title) {
        pendingTitle = title;
        doors.close();
    }

    public void endShow() {
        projector.stop();
        sound.set(0);
        lights.set(100);
        doors.open();
    }

    public void emergency() {
        lights.set(100);
        doors.open();
        sound.set(0);
        adPlayer.stop();
        projector.cut();
    }

    // Reakcje na zdarzenia urządzeń - cała kolejność w jednym miejscu.

    @Override
    public void doorsClosed() {
        lights.set(30);
        adPlayer.start(ADS_BEFORE_FEATURE);
    }

    @Override
    public void doorsBlocked() {
        lights.set(100);
        closeWhenCleared = true;
    }

    @Override
    public void doorsCleared() {
        if (closeWhenCleared) {
            closeWhenCleared = false;
            doors.close();
        }
    }

    @Override
    public void adsFinished() {
        lights.set(0);
        sound.set(70);
        projector.play(pendingTitle);
        pendingTitle = null;
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
