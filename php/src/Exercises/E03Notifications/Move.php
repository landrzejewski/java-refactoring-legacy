<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications;

/** Przeniesienie seansu na inny termin i do innej sali. */
final readonly class Move implements Change
{
    public function __construct(public ?string $newStart, public ?string $newHall)
    {
    }
}
