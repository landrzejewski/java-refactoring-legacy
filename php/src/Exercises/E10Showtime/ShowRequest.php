<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime;

/** Stabilny kontrakt ćwiczenia: seans do uruchomienia w sali - identyfikator (dla bramki i raportu) i tytuł (dla projektora). */
final readonly class ShowRequest
{
    public function __construct(public string $screeningId, public string $title)
    {
    }
}
