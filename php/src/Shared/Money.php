<?php

declare(strict_types=1);

namespace Training\Workshop\Shared;

use BcMath\Number;
use RoundingMode;
use Stringable;

/**
 * Kwota w złotych, zawsze ze skalą 2 i zaokrągleniem HALF_UP (w PHP: RoundingMode::HalfAwayFromZero).
 * Wspólny, nierefaktoryzowany typ wartości warsztatu CineLegacy.
 */
final readonly class Money implements Stringable
{
    public Number $amount;

    public function __construct(Number|string|int $amount)
    {
        $amount = $amount instanceof Number ? $amount : new Number($amount);
        $this->amount = $amount->round(2, RoundingMode::HalfAwayFromZero)->add(0, 2);
    }

    public static function zero(): self
    {
        return new self('0.00');
    }

    public static function of(string|int $amount): self
    {
        return new self($amount);
    }

    public function plus(Money $other): self
    {
        return new self($this->amount->add($other->amount));
    }

    public function minus(Money $other): self
    {
        return new self($this->amount->sub($other->amount));
    }

    public function times(int $factor): self
    {
        return new self($this->amount->mul($factor));
    }

    /** Procent kwoty, np. {@code percent(25)} to 25% tej kwoty. */
    public function percent(int $percent): self
    {
        return new self($this->amount->mul($percent)->div(100, 10));
    }

    public function max(Money $other): self
    {
        return $this->compareTo($other) >= 0 ? $this : $other;
    }

    public function isGreaterThan(Money $other): bool
    {
        return $this->compareTo($other) > 0;
    }

    public function compareTo(Money $other): int
    {
        return $this->amount->compare($other->amount);
    }

    public function equals(Money $other): bool
    {
        return $this->compareTo($other) === 0;
    }

    public function __toString(): string
    {
        return (string) $this->amount;
    }
}
