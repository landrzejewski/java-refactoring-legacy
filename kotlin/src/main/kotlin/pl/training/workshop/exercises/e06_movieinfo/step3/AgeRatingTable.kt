package pl.training.workshop.exercises.e06_movieinfo.step3

import pl.training.workshop.exercises.e06_movieinfo.AgeRatingResource

/**
 * Krok 3: jedna instancja tabeli na JVM. Bezpieczna, bo tabela jest niemutowalna i każda jej kopia
 * byłaby równoważna; enum daje leniwe, bezpieczne wątkowo tworzenie bez synchronizacji.
 * To domyślna, nie jedyna implementacja [AgeRatings] - o cyklu życia decyduje korzeń
 * kompozycji, a test może podać własną tabelę.
 */
enum class AgeRatingTable : AgeRatings {
    INSTANCE;

    private val categories: Map<String, String>

    init {
        val parsed = HashMap<String, String>()
        for (line in AgeRatingResource.readLines()) {
            if (line.isBlank() || line.startsWith("#")) {
                continue
            }
            val parts = line.split(";")
            parsed[parts[0] + ":" + parts[1]] = parts[2]
        }
        categories = parsed.toMap()
    }

    override fun categoryFor(system: String, code: String): String = categories["$system:$code"] ?: "brak"
}
