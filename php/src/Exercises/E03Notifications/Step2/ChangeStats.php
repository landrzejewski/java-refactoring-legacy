<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step2;

/** Krok 2: statystyki jako odbiorca zdarzen. */
final class ChangeStats implements ScreeningListener
{
    private int $moved = 0;
    private int $cancelled = 0;

    public function __construct(private readonly Journal $journal)
    {
    }

    public function screeningMoved(ScreeningMoved $event): void
    {
        $this->moved++;
        $this->journal->note('STATS przeniesione=' . $this->moved . ' odwolane=' . $this->cancelled);
    }

    public function screeningCancelled(ScreeningCancelled $event): void
    {
        $this->cancelled++;
        $this->journal->note('STATS przeniesione=' . $this->moved . ' odwolane=' . $this->cancelled);
    }
}
