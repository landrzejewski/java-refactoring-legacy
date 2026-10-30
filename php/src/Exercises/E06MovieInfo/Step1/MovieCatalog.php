<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step1;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;

/**
 * Krok 1: port w języku kina - preferowany interfejs katalogu filmów. Mówi o minutach, ocenie 0-10
 * i kategorii kina, nie o "155 min", "8.0/10" ani kodach tabeli MOVIES.
 */
interface MovieCatalog
{
    /** Dane filmu albo {@see \InvalidArgumentException} "unknown movie: id" - niezależnie od źródła. */
    public function find(string $id): MovieDetails;
}
