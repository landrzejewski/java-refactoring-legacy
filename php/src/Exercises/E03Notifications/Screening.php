<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications;

/** Stabilny kontrakt sceny: seans w repertuarze (termin jako tekst, np. {@code 2026-10-30 18:00}). */
final readonly class Screening
{
    public function __construct(public string $id, public string $title, public string $start, public string $hall)
    {
    }
}
