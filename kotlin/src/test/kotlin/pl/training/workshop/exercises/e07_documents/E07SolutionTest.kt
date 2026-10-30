package pl.training.workshop.exercises.e07_documents

import java.lang.reflect.Constructor
import java.lang.reflect.Modifier
import kotlin.reflect.KVisibility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test

import pl.training.workshop.exercises.e07_documents.step2.ChannelDocument
import pl.training.workshop.exercises.e07_documents.step2.CodeRenderer
import pl.training.workshop.exercises.e07_documents.step2.Document
import pl.training.workshop.exercises.e07_documents.step2.DocumentComposer
import pl.training.workshop.exercises.e07_documents.step2.DocumentFactories
import pl.training.workshop.exercises.e07_documents.step2.DocumentFactory
import pl.training.workshop.exercises.e07_documents.step2.LoyaltyFooter
import pl.training.workshop.exercises.e07_documents.step2.PromoBanner
import pl.training.workshop.exercises.e07_documents.step2.ReceiptLayout
import pl.training.workshop.exercises.e07_documents.step2.TicketLayout
import pl.training.workshop.exercises.e07_documents.step2.VatSection
import pl.training.workshop.shared.Money

/**
 * Pułapka start (zestaw da się pomieszać) i kontrakt rozwiązania: spójna rodzina na kanał,
 * nowy kanał jedną klasą, sekcje owijane w kolejności zapisanej w jednym miejscu.
 */
class E07SolutionTest {
    /** Pomiar w start: trzy luźne części można pomieszać; po "warsztat.sh jump" test jest pominięty. */
    @Test
    fun startLetsYouMixTicketCodeAndReceiptOfDifferentChannels() {
        val loose: Constructor<*>? = try {
            Class.forName(START + "DocumentComposer").getConstructor(
                Class.forName(START + "TicketLayout"),
                Class.forName(START + "CodeRenderer"),
                Class.forName(START + "ReceiptLayout")
            )
        } catch (exception: ReflectiveOperationException) {
            null
        }
        assumeTrue(loose != null, "start nie przyjmuje już trzech luźnych części")
        val mixed = loose!!.newInstance(
            Class.forName(START + "KioskTicketLayout").getConstructor().newInstance(),
            Class.forName(START + "MobileCodeRenderer").getConstructor().newInstance(),
            Class.forName(START + "BoxOfficeReceiptLayout").getConstructor().newInstance()
        )
        val document = mixed.javaClass.getMethod("compose", Sale::class.java).invoke(mixed, SALE) as String
        assertTrue(
            document.contains("********") && document.contains("<qr>") && document.contains("PARAGON FISKALNY"),
            "kiosk + QR aplikacji + paragon A4 w jednym dokumencie:\n$document"
        )
    }

    @Test
    fun solutionHidesConcreteDocumentsBehindFamilies() {
        // Różnica względem Javy: konkretne produkty są `internal`, a `internal` w bajtkodzie to klasa
        // publiczna, więc Modifier.isPublic nic tu nie powie. Widoczność w sensie Kotlina podaje kotlin-reflect.
        for (prefix in listOf("Kiosk", "Mobile", "BoxOffice")) {
            for (product in listOf("TicketLayout", "CodeRenderer", "ReceiptLayout")) {
                val concrete = Class.forName(STEP2 + prefix + product).kotlin
                assertFalse(concrete.visibility == KVisibility.PUBLIC, "${concrete.simpleName} jest publiczna")
            }
            assertEquals(KVisibility.PUBLIC, Class.forName(STEP2 + prefix + "Documents").kotlin.visibility)
        }
        assertEquals(KVisibility.PUBLIC, DocumentFactory::class.visibility)
    }

    @Test
    fun everyFamilyDeliversPartsOfOneChannelOnly() {
        for (channel in Channel.entries) {
            val family = DocumentFactories.forChannel(channel)
            val prefix = family.ticketLayout().javaClass.simpleName.replace("TicketLayout", "")
            assertEquals(prefix + "CodeRenderer", family.codeRenderer().javaClass.simpleName)
            assertEquals(prefix + "ReceiptLayout", family.receiptLayout().javaClass.simpleName)
        }
    }

    @Test
    fun newChannelIsOneFamilyWithoutTouchingTheComposer() {
        val email = object : DocumentFactory {
            override fun ticketLayout(): TicketLayout = TicketLayout { sale -> "Bilet " + sale.number + " na " + sale.movie }

            override fun codeRenderer(): CodeRenderer = CodeRenderer { sale -> "[" + sale.number + "]" }

            override fun receiptLayout(): ReceiptLayout = object : ReceiptLayout {
                override fun receipt(sale: Sale): String = "Razem " + sale.ticketPrice.plus(sale.barPrice)

                override fun section(lines: List<String>): String = "> " + lines.joinToString(" | ")
            }
        }
        val document = DocumentComposer(email).compose(SALE, PrintOptions.ALL).render()
        assertEquals(
            """
            Bilet S-1001 na Diuna
            [S-1001]
            Razem 40.00
            > VAT 8% bilet: 2.96
            > Kod KINO10: -10% na nastepny bilet online
            > Punkty: 96 + 4 = 100 | Odbierz darmowy bilet 2D
            """.trimIndent(), document
        )
    }

    @Test
    fun wrappingOrderIsBehaviourAndIsFixedInTheComposer() {
        val kiosk = DocumentFactories.forChannel(Channel.KIOSK)
        val layout = kiosk.receiptLayout()
        val core: Document = ChannelDocument(SALE, kiosk)
        val vatThenLoyalty: Document = LoyaltyFooter(VatSection(core, layout, SALE), layout, SALE)
        val loyaltyThenVat: Document = VatSection(LoyaltyFooter(core, layout, SALE), layout, SALE)
        assertNotEquals(vatThenLoyalty.render(), loyaltyThenVat.render(), "kolejność owijania zmienia dokument")

        val composed = DocumentComposer(kiosk).compose(SALE, PrintOptions(true, false, true)).render()
        assertEquals(vatThenLoyalty.render(), composed, "composer owija w kolejności VAT, promocja, stopka")
    }

    @Test
    fun everySectionWrapsExactlyOneInnerDocument() {
        for (section in listOf(VatSection::class.java, PromoBanner::class.java, LoyaltyFooter::class.java)) {
            assertTrue(Document::class.java.isAssignableFrom(section), "${section.simpleName} ma kontrakt dokumentu")
            val inner = section.declaredFields.count { it.type == Document::class.java }
            assertEquals(1, inner, section.simpleName)
        }
        assertEquals(0, ChannelDocument::class.java.declaredFields.count { it.type == Document::class.java }, "rdzeń niczego nie owija")
    }

    companion object {
        private const val START = "pl.training.workshop.exercises.e07_documents.start."
        private const val STEP2 = "pl.training.workshop.exercises.e07_documents.step2."
        private val SALE = Sale("S-1001", "Diuna", "R10-5", Money.of("40.00"), Money.ZERO, 96)
    }
}
