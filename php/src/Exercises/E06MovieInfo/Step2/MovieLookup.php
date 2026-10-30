<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Krok 2: Extract Interface z serwisu. Ten sam kontrakt ma prawdziwy serwis i strażnik uprawnień,
 * który go owija - klient nie wie, z którym rozmawia.
 */
interface MovieLookup
{
    public function lookup(User $user, MovieSource $source, string $id): MovieDetails;

    public function runtime(User $user, MovieSource $source, string $id): int;
}
