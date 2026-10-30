package pl.training.workshop.exercises.e07_documents.step2

import pl.training.workshop.exercises.e07_documents.Channel

/**
 * Krok 2: jedyny `when` po kanale. Fabryki są bezstanowe, więc współdzielimy jedne instancje.
 * Nowy kanał to jedna nowa klasa fabryki i jeden wpis tutaj.
 */
object DocumentFactories {
    private val KIOSK: DocumentFactory = KioskDocuments()
    private val MOBILE: DocumentFactory = MobileDocuments()
    private val BOX_OFFICE: DocumentFactory = BoxOfficeDocuments()

    fun forChannel(channel: Channel): DocumentFactory = when (channel) {
        Channel.KIOSK -> KIOSK
        Channel.MOBILE -> MOBILE
        Channel.BOX_OFFICE -> BOX_OFFICE
    }
}
