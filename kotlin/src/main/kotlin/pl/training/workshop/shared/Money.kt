package pl.training.workshop.shared

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Kwota w złotych, zawsze ze skalą 2 i zaokrągleniem HALF_UP.
 * Wspólny, nierefaktoryzowany typ wartości warsztatu CineLegacy.
 */
class Money(amount: BigDecimal) : Comparable<Money> {
    val amount: BigDecimal = amount.setScale(2, RoundingMode.HALF_UP)

    operator fun plus(other: Money): Money = Money(amount.add(other.amount))

    operator fun minus(other: Money): Money = Money(amount.subtract(other.amount))

    operator fun times(factor: Int): Money = Money(amount.multiply(BigDecimal.valueOf(factor.toLong())))

    /** Procent kwoty, np. `percent(25)` to 25% tej kwoty. */
    fun percent(percent: Int): Money = Money(
        amount.multiply(BigDecimal.valueOf(percent.toLong()))
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
    )

    fun max(other: Money): Money = if (this >= other) this else other

    fun isGreaterThan(other: Money): Boolean = this > other

    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    override fun equals(other: Any?): Boolean = other is Money && amount == other.amount

    override fun hashCode(): Int = amount.hashCode()

    override fun toString(): String = amount.toPlainString()

    companion object {
        val ZERO: Money = of("0.00")

        fun of(amount: String): Money = Money(BigDecimal(amount))

        fun of(amount: Long): Money = Money(BigDecimal.valueOf(amount))
    }
}
