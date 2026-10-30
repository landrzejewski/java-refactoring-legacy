<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Start;

/** Start: statystyki zmian repertuaru - licznik przeniesien i odwolan wypisywany po kazdej zmianie. */
final class ChangeStats
{
    private int $moved = 0;
    private int $cancelled = 0;

    public function __construct(private readonly Journal $journal)
    {
    }

    public function moved(): void
    {
        $this->moved++;
        $this->journal->note('STATS przeniesione=' . $this->moved . ' odwolane=' . $this->cancelled);
    }

    public function cancelled(): void
    {
        $this->cancelled++;
        $this->journal->note('STATS przeniesione=' . $this->moved . ' odwolane=' . $this->cancelled);
    }
}
