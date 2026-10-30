<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker;

/** Stabilny kontrakt sceny: układ sali i skrypt operacji kasjera, np. "SELECT 6-4", "UNDO". */
final readonly class PickerRun
{
    /** @param list<string> $script */
    public function __construct(public HallPlan $plan, public array $script)
    {
    }

    public static function standard(string ...$script): self
    {
        return new self(HallPlan::standard(), $script);
    }

    public static function almostFull(string ...$script): self
    {
        return new self(HallPlan::almostFull(), $script);
    }
}
