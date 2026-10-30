<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\User;

/** Krok 3: wspólny kontrakt serwisu i strażnika uprawnień - bez zmian. */
interface MovieLookup
{
    public function lookup(User $user, MovieSource $source, string $id): MovieDetails;

    public function runtime(User $user, MovieSource $source, string $id): int;
}
