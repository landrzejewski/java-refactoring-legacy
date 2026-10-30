<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

/** Krok 2: surowe pola jednego wiersza - produkt parsera, bez względu na format pliku. */
final readonly class RawScreening
{
    public function __construct(public string $title, public string $format, public string $hall, public string $start)
    {
    }
}
