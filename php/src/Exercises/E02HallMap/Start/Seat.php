<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Start;

use Training\Workshop\Shared\Money;

/**
 * Start: miejsce niesie własną kopię wszystkiego, co opisuje jego kategorię. Dwa fotele VIP
 * w tym samym rzędzie mają dwie osobne kopie nazwy, dopłaty, ikony i opisu.
 */
final readonly class Seat
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
