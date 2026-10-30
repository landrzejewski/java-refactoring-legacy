<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step1;

use Training\Workshop\Shared\Money;

/**
 * Krok 1: miejsce jest liściem drzewa - jego pojemność to 1, a dopłata to jego własna dopłata.
 * Dane kategorii nadal są kopiowane do każdego miejsca (to osobny zapach, krok 3).
 */
final readonly class Seat implements HallElement
{
    public function __construct(
        private string $label,
        private string $code,
        private string $categoryName,
        private Money $surcharge,
        private string $icon,
        private string $description,
    ) {
    }

    public function capacity(): int
    {
        return 1;
    }

    public function surcharges(): Money
    {
        return $this->surcharge;
    }

    public function label(): string
    {
        return $this->label;
    }

    public function code(): string
    {
        return $this->code;
    }

    public function categoryName(): string
    {
        return $this->categoryName;
    }

    public function surcharge(): Money
    {
        return $this->surcharge;
    }

    public function icon(): string
    {
        return $this->icon;
    }

    public function description(): string
    {
        return $this->description;
    }
}
