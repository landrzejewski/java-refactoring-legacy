<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;

/** Krok 3: port katalogu bez zmian. */
interface MovieCatalog
{
    /** Dane filmu albo {@see \InvalidArgumentException} "unknown movie: id" - niezależnie od źródła. */
    public function find(string $id): MovieDetails;
}
