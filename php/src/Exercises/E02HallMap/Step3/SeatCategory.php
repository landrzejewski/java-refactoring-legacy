<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step3;

use Training\Workshop\Shared\Money;

/**
 * Krok 3: kategoria miejsca - stan wspólny wielu miejsc, niemutowalny, więc bezpieczny do
 * współdzielenia. Nie wie, w którym rzędzie ani pod jakim numerem stoi fotel.
 */
final readonly class SeatCategory
{
    public function __construct(
        private string $code,
        private string $name,
        private Money $surcharge,
        private string $icon,
        private string $description,
    ) {
    }

    public function code(): string
    {
        return $this->code;
    }

    public function name(): string
    {
        return $this->name;
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
