package pl.training.workshop.exercises.e01_import.step1

/** Krok 1: import CSV to już tylko rozpoznanie komentarza i podział wiersza na cztery pola. */
class CsvRepertoireImporter : RepertoireImporter() {
    override fun isComment(line: String): Boolean = line.startsWith("#")

    override fun parseLine(line: String, number: Int): RawScreening {
        val fields = line.split(";")
        if (fields.size != 4) {
            throw IllegalArgumentException("line $number: expected 4 fields, got ${fields.size}")
        }
        return RawScreening(fields[0].trim(), fields[1].trim(), fields[2].trim(), fields[3].trim())
    }
}
