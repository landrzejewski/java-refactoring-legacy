package pl.training.workshop.exercises.e10_showtime.start

/**
 * Start: drzwi sali. Oprócz własnej logiki (zamknięte/otwarte, przeszkoda w świetle drzwi) znają
 * oświetlenie i odtwarzacz reklam, bo po zamknięciu "wiedzą", że trzeba przyciemnić i puścić reklamy.
 * Żeby to zrobić, muszą pamiętać tytuł filmu, który nie ma nic wspólnego z drzwiami.
 */
class Doors(private val log: DeviceLog) {
    private lateinit var lights: Lights
    private lateinit var adPlayer: AdPlayer
    private var closed = false
    private var blocked = false
    private var pendingTitle: String? = null

    val isClosed: Boolean
        get() = closed

    fun wire(lights: Lights, adPlayer: AdPlayer) {
        this.lights = lights
        this.adPlayer = adPlayer
    }

    fun close(title: String) {
        if (closed) {
            throw IllegalStateException("doors already closed")
        }
        if (blocked) {
            log.add("doors: blocked")
            lights.set(100)
            pendingTitle = title
            return
        }
        closed = true
        log.add("doors: closed")
        lights.set(30)
        adPlayer.start(2, title)
    }

    fun open() {
        closed = false
        log.add("doors: open")
    }

    /** Czujnik: coś stoi w świetle drzwi. */
    fun obstacle() {
        blocked = true
        log.add("doors: obstacle")
    }

    /** Czujnik: przejście wolne - jeśli zamknięcie czekało, drzwi same je wznawiają. */
    fun cleared() {
        if (!blocked) {
            throw IllegalStateException("doors are not blocked")
        }
        blocked = false
        log.add("doors: cleared")
        val title = pendingTitle
        if (title != null) {
            pendingTitle = null
            close(title)
        }
    }
}
