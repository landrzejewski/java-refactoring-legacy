package pl.training.workshop.exercises.e01_import

import java.lang.reflect.Modifier

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

import pl.training.workshop.exercises.e01_import.step2.CsvRepertoireImporter
import pl.training.workshop.exercises.e01_import.step2.Importers
import pl.training.workshop.exercises.e01_import.step2.JsonLineParser
import pl.training.workshop.exercises.e01_import.step2.JsonLinesRepertoireImporter
import pl.training.workshop.exercises.e01_import.step2.LineParser
import pl.training.workshop.exercises.e01_import.step2.RawScreening
import pl.training.workshop.exercises.e01_import.step2.RepertoireImporter

/**
 * Kontrakt rozwiązania: szkielet w jednym miejscu i chroniony, krok zależny od formatu jako osobny
 * produkt, importer konkretnego formatu tylko tworzy i podpisuje, a rozszerzenia zna jedno miejsce.
 */
class E01SolutionTest {
    @Test
    fun skeletonIsFinalSoNoFormatCanReorderTheSteps() {
        val template = RepertoireImporter::class.java.getMethod("importLines", List::class.java)
        assertTrue(Modifier.isFinal(template.modifiers))
    }

    @Test
    fun newFormatIsOneParserAndOneCreatorAndReusesSortingAndPricing() {
        val tsv = object : RepertoireImporter() {
            override fun label(): String = "TSV"

            override fun createLineParser(): LineParser = object : LineParser {
                override fun isComment(line: String): Boolean = line.startsWith(";")

                override fun parse(line: String, number: Int): RawScreening {
                    val fields = line.split("\t")
                    return RawScreening(fields[0], fields[1], fields[2], fields[3])
                }
            }
        }
        val entries = tsv.importLines(listOf("; komentarz", "Diuna\tIMAX\t1\t20:30", "Amator\t2D\t3\t09:00"))
        assertEquals("[09:00 Amator (2D, sala 3) 20.00, 20:30 Diuna (IMAX, sala 1) 40.00]", entries.toString())
    }

    @Test
    fun lineParserIsTestableWithoutAnyImporter() {
        val parser = JsonLineParser()
        assertEquals(
            RawScreening("Diuna", "IMAX", "1", "20:30"),
            parser.parse("""{"title": "Diuna", "format": "IMAX", "hall": "1", "start": "20:30"}""", 7)
        )
        val error = assertThrows<IllegalArgumentException> { parser.parse("""{"title": "Diuna"}""", 7) }
        assertEquals("line 7: missing field: format", error.message)
    }

    @Test
    fun concreteImportersOnlyCreateAndLabel() {
        for (creator in listOf(CsvRepertoireImporter::class.java, JsonLinesRepertoireImporter::class.java)) {
            val declared = creator.declaredMethods
                .filterNot { it.isSynthetic }
                .map { it.name }
                .toSet()
            assertEquals(setOf("label", "createLineParser"), declared, creator.simpleName)
        }
    }

    @Test
    fun fileExtensionsAreKnownInOnePlace() {
        assertEquals("CSV", Importers.forFile("piatek.csv").label())
        assertEquals("JSONL", Importers.forFile("piatek.jsonl").label())
        val error = assertThrows<IllegalArgumentException> { Importers.forFile("piatek.xml") }
        assertEquals("unsupported file: piatek.xml", error.message)
    }
}
