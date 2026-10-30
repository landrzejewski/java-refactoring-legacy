package pl.training.workshop.support

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest

/**
 * Pomocnik testów równoważności scen warsztatu.
 * Każdy wariant sceny (start, step1, ...) jest adaptowany do wspólnej
 * funkcji `(I) -> O`, a następnie sprawdzany na tych samych przypadkach.
 *
 * ```
 * @TestFactory
 * fun everyVariantBehavesTheSame(): List<DynamicTest> =
 *     Scene.variants<Order, String>()
 *         .variant("start") { start.PriceCalculator().price(it) }
 *         .variant("step1") { step1.PriceCalculator().price(it) }
 *         .expect("2D normal", order2d, "25.00")
 *         .tests()
 * ```
 */
class Scene<I, O> private constructor() {
    private val variants = linkedMapOf<String, (I) -> O>()
    private val cases = mutableListOf<Case<I, O>>()

    fun variant(name: String, implementation: (I) -> O): Scene<I, O> {
        variants[name] = implementation
        return this
    }

    fun expect(name: String, input: I, expected: O): Scene<I, O> {
        cases += Case(name, input, expected)
        return this
    }

    fun tests(): List<DynamicTest> = variants.flatMap { (variant, implementation) ->
        cases.map { case ->
            DynamicTest.dynamicTest("$variant: ${case.name}") {
                assertEquals(case.expected, implementation(case.input))
            }
        }
    }

    private data class Case<I, O>(val name: String, val input: I, val expected: O)

    companion object {
        fun <I, O> variants(): Scene<I, O> = Scene()
    }
}
