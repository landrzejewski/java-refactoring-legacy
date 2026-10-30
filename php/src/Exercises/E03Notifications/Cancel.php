<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications;

/** Odwolanie seansu z podanego powodu. */
final readonly class Cancel implements Change
{
    public function __construct(public ?string $reason)
    {
    }
}
