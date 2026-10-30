<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step1;

use Training\Workshop\Shared\Money;

/**
 * Krok 1: węzeł drzewa sali. Sumuje odpowiedzi dzieci, nie wiedząc, czy dziecko to rząd,
 * sektor czy miejsce. Lista dzieci jest ustalana raz i niemutowalna (wariant bezpieczny).
 */
abstract class HallGroup implements HallElement
{
    /** @param list<HallElement> $children */
    protected function __construct(private readonly array $children)
    {
    }

    final public function capacity(): int
    {
        $capacity = 0;
        foreach ($this->children as $child) {
            $capacity += $child->capacity();
        }
        return $capacity;
    }

    final public function surcharges(): Money
    {
        $surcharges = Money::zero();
        foreach ($this->children as $child) {
            $surcharges = $surcharges->plus($child->surcharges());
        }
        return $surcharges;
    }

    /** @return list<HallElement> */
    public function children(): array
    {
        return $this->children;
    }
}
