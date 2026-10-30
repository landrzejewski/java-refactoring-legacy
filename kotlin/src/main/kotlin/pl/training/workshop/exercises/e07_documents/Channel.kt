package pl.training.workshop.exercises.e07_documents

/** Stabilny kontrakt sceny: kanał sprzedaży, dla którego drukujemy dokumenty. */
enum class Channel {
    /** Kiosk samoobsługowy: drukarka termiczna, 32 kolumny, kod kreskowy. */
    KIOSK,

    /** Aplikacja mobilna: dokument HTML-podobny, kod QR. */
    MOBILE,

    /** Kasa: wydruk A4, kod kontrolny do odczytu wzrokowego. */
    BOX_OFFICE
}
