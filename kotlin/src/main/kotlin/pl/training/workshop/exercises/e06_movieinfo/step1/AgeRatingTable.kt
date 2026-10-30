package pl.training.workshop.exercises.e06_movieinfo.step1

import pl.training.workshop.exercises.e06_movieinfo.AgeRatingResource

/** Krok 1: tabela kategorii wiekowych bez zmian - nadal wczytywana przy każdym wywołaniu. */
class AgeRatingTable private constructor(private val categories: Map<String, String>) {
    /** Kategoria kina dla kodu z obcego systemu; nieznany kod to "brak". */
    fun categoryFor(system: String, code: String): String = categories["$system:$code"] ?: "brak"

    companion object {
        fun load(): AgeRatingTable {
            val parsed = HashMap<String, String>()
            for (line in AgeRatingResource.readLines()) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue
                }
                val parts = line.split(";")
                parsed[parts[0] + ":" + parts[1]] = parts[2]
            }
            return AgeRatingTable(parsed.toMap())
        }
    }
}
