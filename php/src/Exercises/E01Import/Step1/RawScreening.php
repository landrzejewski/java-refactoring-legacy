<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step1;

/** Krok 1: surowe pola jednego wiersza, zanim przejdą wspólną walidację (bez względu na format pliku). */
final readonly class RawScreening
{
    public function __construct(public string $title, public string $format, public string $hall, public string $start)
    {
    }
}
