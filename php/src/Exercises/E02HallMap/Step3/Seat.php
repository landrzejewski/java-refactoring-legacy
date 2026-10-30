<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step3;

use Training\Workshop\Shared\Money;

/**
 * Krok 3: miejsce to tylko etykieta (stan własny) i odwołanie do współdzielonej kategorii
 * (stan wspólny). 400 foteli VIP wskazuje na jeden obiekt kategorii.
 */
final readonly class Seat implements HallElement
{
    public function __construct(private string $label, private SeatCategory $category)
    {
    }

    public function capacity(): int
    {
        return 1;
    }

    public function surcharges(): Money
    {
        return $this->category->surcharge();
    }

    public function label(): string
    {
        return $this->label;
    }

    public function category(): SeatCategory
    {
        return $this->category;
    }
}
