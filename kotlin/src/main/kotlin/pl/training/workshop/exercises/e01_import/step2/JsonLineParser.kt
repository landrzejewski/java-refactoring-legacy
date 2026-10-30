package pl.training.workshop.exercises.e01_import.step2

/** Krok 2: parser jednego obiektu JSON z wiersza pliku JSON Lines, komentarze od `//`. */
class JsonLineParser : LineParser {
    override fun isComment(line: String): Boolean = line.startsWith("//")

    override fun parse(line: String, number: Int): RawScreening {
        if (!line.startsWith("{") || !line.endsWith("}")) {
            throw IllegalArgumentException("line $number: not a JSON object")
        }
        val fields = linkedMapOf<String, String>()
        for (match in JSON_FIELD.findAll(line)) {
            fields[match.groupValues[1]] = match.groupValues[2]
        }
        for (required in listOf("title", "format", "hall", "start")) {
            if (required !in fields) {
                throw IllegalArgumentException("line $number: missing field: $required")
            }
        }
        return RawScreening(fields.getValue("title"), fields.getValue("format"), fields.getValue("hall"), fields.getValue("start"))
    }

    private companion object {
        val JSON_FIELD = Regex("\"(\\w+)\"\\s*:\\s*\"([^\"]*)\"")
    }
}
