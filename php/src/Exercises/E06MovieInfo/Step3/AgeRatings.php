<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

/** Krok 3: Extract Interface - adaptery zależą od kontraktu tabeli, nie od jedynej instancji. */
interface AgeRatings
{
    /** Kategoria kina dla kodu z obcego systemu; nieznany kod to "brak". */
    public function categoryFor(string $system, string $code): string;
}
