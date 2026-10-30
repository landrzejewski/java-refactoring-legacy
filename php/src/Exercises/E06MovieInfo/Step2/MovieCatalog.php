<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;

/** Krok 2: port katalogu bez zmian - ten sam interfejs ma adapter i owijająca go pamięć podręczna. */
interface MovieCatalog
{
    /** Dane filmu albo {@see \InvalidArgumentException} "unknown movie: id" - niezależnie od źródła. */
    public function find(string $id): MovieDetails;
}
