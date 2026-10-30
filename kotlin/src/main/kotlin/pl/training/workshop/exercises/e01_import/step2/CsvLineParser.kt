package pl.training.workshop.exercises.e01_import.step2

/** Krok 2: parser wiersza CSV `tytuł;format;sala;start`, komentarze od `#`. */
class CsvLineParser : LineParser {
    override fun isComment(line: String): Boolean = line.startsWith("#")

    override fun parse(line: String, number: Int): RawScreening {
        val fields = line.split(";")
        if (fields.size != 4) {
            throw IllegalArgumentException("line $number: expected 4 fields, got ${fields.size}")
        }
        return RawScreening(fields[0].trim(), fields[1].trim(), fields[2].trim(), fields[3].trim())
    }
}
