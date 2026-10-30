package pl.training.workshop.exercises.e10_showtime.step2

/**
 * Krok 2: sterownik sali jest jedynym miejscem, które zna wszystkie urządzenia i kolejność ich reakcji.
 * Urządzenia zgłaszają zdarzenia przez [HallMediator], a sterownik decyduje, co dalej.
 * Logika urządzeń (limit głośności, przeszkoda w drzwiach, stan projektora) zostaje w urządzeniach.
 */
internal class HallController(log: DeviceLog) : HallMediator {
    private val lights = Lights(log)
    private val sound = Sound(log)
    private val doors = Doors(log, this)
    private val adPlayer = AdPlayer(log, this)
    private val projector = Projector(log)
    private var pendingTitle: String? = null
    private var closeWhenCleared = false

    // Polecenia klientów sali.

    fun startShow(title: String) {
        pendingTitle = title
        doors.close()
    }

    fun endShow() {
        projector.stop()
        sound.set(0)
        lights.set(100)
        doors.open()
    }

    fun emergency() {
        lights.set(100)
        doors.open()
        sound.set(0)
        adPlayer.stop()
        projector.cut()
    }

    // Reakcje na zdarzenia urządzeń - cała kolejność w jednym miejscu.

    override fun doorsClosed() {
        lights.set(30)
        adPlayer.start(ADS_BEFORE_FEATURE)
    }

    override fun doorsBlocked() {
        lights.set(100)
        closeWhenCleared = true
    }

    override fun doorsCleared() {
        if (closeWhenCleared) {
            closeWhenCleared = false
            doors.close()
        }
    }

    override fun adsFinished() {
        lights.set(0)
        sound.set(70)
        projector.play(pendingTitle)
        pendingTitle = null
    }

    // Zdarzenia sprzętu (w teście symulowane poleceniami skryptu).

    fun adTick() {
        adPlayer.tick()
    }

    fun doorObstacle() {
        doors.obstacle()
    }

    fun doorCleared() {
        doors.cleared()
    }

    fun volume(level: Int) {
        sound.set(level)
    }

    private companion object {
        const val ADS_BEFORE_FEATURE = 2
    }
}
