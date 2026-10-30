package pl.training.workshop.exercises.e09_cashdrawer

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

import pl.training.workshop.shared.Money
import pl.training.workshop.support.Scene

/**
 * Raport zamknięcia kasy identyczny na każdym kroku: każdy rodzaj wpisu, każdy tryb podziału,
 * zaokrąglenia, które muszą sumować się do rabatu, i błędy danych.
 */
class E09EquivalenceTest {
    @TestFactory
    fun everyStepRendersTheSameReport(): List<DynamicTest> = Scene.variants<DrawerDay, String>()
        .variant("start") { day -> report { pl.training.workshop.exercises.e09_cashdrawer.start.CashDrawerReport().render(day) } }
        .variant("step1") { day -> report { pl.training.workshop.exercises.e09_cashdrawer.step1.CashDrawerReport().render(day) } }
        .variant("step2") { day -> report { pl.training.workshop.exercises.e09_cashdrawer.step2.CashDrawerReport().render(day) } }
        .expect(
            "dzien mieszany, grupa 10 x 25.00 proporcjonalnie",
            DrawerDay(MIXED_DAY, tickets(10, "25.00"), "PROPORTIONAL"),
            """
            Gotowka w szufladzie: 240.00
            Eksport fiskalny:
            S;T-101;25.00;8
            K;T-102;32.00;8;4321
            Z;T-101;-25.00;8
            S;T-103;40.00;8
            Audyt:
            CASH T-101 25.00
            CARD T-102 32.00 karta ****4321
            DEPOSIT 200.00 pogotowie kasowe
            REFUND T-101 -25.00 powod: choroba
            CASH T-103 40.00
            Rabat grupowy PROPORTIONAL: 25.00 = 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50 + 2.50
            
            """.trimIndent()
        )
        .expect(
            "tylko karty, bez grupy",
            DrawerDay(listOf(card("T-201", "40.00", "1111"), card("T-202", "15.00", "2222")), listOf(), "PROPORTIONAL"),
            """
            Gotowka w szufladzie: 0.00
            Eksport fiskalny:
            K;T-201;40.00;8;1111
            K;T-202;15.00;8;2222
            Audyt:
            CARD T-201 40.00 karta ****1111
            CARD T-202 15.00 karta ****2222
            Rabat grupowy: brak (0 biletow)
            
            """.trimIndent()
        )
        .expect(
            "zwroty przewyzszaja sprzedaz gotowkowa",
            DrawerDay(
                listOf(cash("T-301", "25.00"), refund("T-290", "40.00", "odwolany seans"), refund("T-291", "40.00", "odwolany seans")),
                tickets(9, "25.00"),
                "EQUAL"
            ),
            """
            Gotowka w szufladzie: -55.00
            Eksport fiskalny:
            S;T-301;25.00;8
            Z;T-290;-40.00;8
            Z;T-291;-40.00;8
            Audyt:
            CASH T-301 25.00
            REFUND T-290 -40.00 powod: odwolany seans
            REFUND T-291 -40.00 powod: odwolany seans
            Rabat grupowy: brak (9 biletow)
            
            """.trimIndent()
        )
        .expect(
            "pusty dzien",
            DrawerDay(listOf(), listOf(), "LAST"),
            """
            Gotowka w szufladzie: 0.00
            Eksport fiskalny:
            Audyt:
            Rabat grupowy: brak (0 biletow)
            
            """.trimIndent()
        )
        .expect(
            "EQUAL: 30.30 na 11 biletow, 5 groszy reszty dla pierwszych",
            DrawerDay(listOf(), mixed(7, "25.00", 4, "32.00"), "EQUAL"),
            onlyGroup("Rabat grupowy EQUAL: 30.30 = 2.76 + 2.76 + 2.76 + 2.76 + 2.76" + " + 2.75 + 2.75 + 2.75 + 2.75 + 2.75 + 2.75")
        )
        .expect(
            "EQUAL: dokladnie podzielne",
            DrawerDay(listOf(), tickets(10, "25.00"), "EQUAL"),
            onlyGroup("Rabat grupowy EQUAL: 25.00 = 2.50 + 2.50 + 2.50 + 2.50 + 2.50" + " + 2.50 + 2.50 + 2.50 + 2.50 + 2.50")
        )
        .expect(
            "PROPORTIONAL: ceny z groszami, roznica zaokraglen na ostatnim",
            DrawerDay(listOf(), tickets(10, "21.35"), "PROPORTIONAL"),
            onlyGroup("Rabat grupowy PROPORTIONAL: 21.35 = 2.14 + 2.14 + 2.14 + 2.14 + 2.14" + " + 2.14 + 2.14 + 2.14 + 2.14 + 2.09")
        )
        .expect(
            "PROPORTIONAL: rozne ceny",
            DrawerDay(listOf(), mixed(7, "25.00", 4, "32.00"), "PROPORTIONAL"),
            onlyGroup("Rabat grupowy PROPORTIONAL: 30.30 = 2.50 + 2.50 + 2.50 + 2.50 + 2.50" + " + 2.50 + 2.50 + 3.20 + 3.20 + 3.20 + 3.20")
        )
        .expect(
            "LAST: caly rabat na ostatnim bilecie",
            DrawerDay(listOf(), mixed(7, "25.00", 4, "32.00"), "LAST"),
            onlyGroup("Rabat grupowy LAST: 30.30 = 0.00 + 0.00 + 0.00 + 0.00 + 0.00" + " + 0.00 + 0.00 + 0.00 + 0.00 + 0.00 + 30.30")
        )
        .expect(
            "grupa 9 biletow bez rabatu",
            DrawerDay(listOf(), tickets(9, "40.00"), "PROPORTIONAL"),
            onlyGroup("Rabat grupowy: brak (9 biletow)")
        )
        .expect(
            "nieznany tryb podzialu",
            DrawerDay(MIXED_DAY, tickets(10, "25.00"), "RANDOM"),
            "ERROR: unknown split mode: RANDOM"
        )
        .expect(
            "nieznany rodzaj wpisu",
            DrawerDay(listOf(cash("T-1", "25.00"), DrawerLine("VOUCHER", "V-1", Money.of("20.00"), "")), tickets(10, "25.00"), "LAST"),
            "ERROR: unknown entry kind: VOUCHER"
        )
        .tests()

    companion object {
        private val MIXED_DAY = listOf(
            cash("T-101", "25.00"),
            card("T-102", "32.00", "4321"),
            deposit("200.00", "pogotowie kasowe"),
            refund("T-101", "25.00", "choroba"),
            cash("T-103", "40.00")
        )

        fun report(report: () -> String): String = try {
            report()
        } catch (exception: IllegalArgumentException) {
            "ERROR: ${exception.message}"
        }

        fun tickets(count: Int, price: String): List<Money> = List(count) { Money.of(price) }

        fun mixed(firstCount: Int, firstPrice: String, secondCount: Int, secondPrice: String): List<Money> =
            tickets(firstCount, firstPrice) + tickets(secondCount, secondPrice)

        private fun onlyGroup(groupLine: String): String = "Gotowka w szufladzie: 0.00\nEksport fiskalny:\nAudyt:\n$groupLine\n"

        private fun cash(reference: String, amount: String) = DrawerLine("CASH", reference, Money.of(amount), "")

        private fun card(reference: String, amount: String, last4: String) = DrawerLine("CARD", reference, Money.of(amount), last4)

        private fun refund(reference: String, amount: String, reason: String) = DrawerLine("REFUND", reference, Money.of(amount), reason)

        private fun deposit(amount: String, note: String) = DrawerLine("DEPOSIT", "", Money.of(amount), note)
    }
}
