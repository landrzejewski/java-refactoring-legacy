package pl.training.workshop.exercises.e10_showtime.start

/**
 * Start: sterownik sali tylko składa urządzenia i spina je ze sobą ("każde z każdym"), a potem
 * przekazuje polecenie pierwszemu urządzeniu łańcucha. Kolejność reakcji jest rozsiana po
 * Doors, AdPlayer i Projector; tylko procedura awaryjna jest zapisana tutaj.
 */
class HallController(log: DeviceLog) {
    private val lights = Lights(log)
    private val sound = Sound(log)
    private val doors = Doors(log)
    private val adPlayer = AdPlayer(log)
    private val projector = Projector(log)

    init {
        doors.wire(lights, adPlayer)
        adPlayer.wire(lights, sound, projector)
        projector.wire(sound, lights, doors)
    }

    fun startShow(title: String) {
        doors.close(title)
    }

    fun endShow() {
        projector.stop()
    }

    fun emergency() {
        lights.set(100)
        doors.open()
        sound.set(0)
        adPlayer.stop()
        projector.cut()
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
}
