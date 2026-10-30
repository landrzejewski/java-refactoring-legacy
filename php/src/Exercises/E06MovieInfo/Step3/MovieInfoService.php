<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\User;

/** Krok 3: prawdziwy podmiot - wybiera katalog dla źródła, nic więcej. Bez zmian. */
final readonly class MovieInfoService implements MovieLookup
{
    /** @param array<string, MovieCatalog> $catalogs katalog dla każdego źródła, klucz to {@code MovieSource->name} */
    public function __construct(private array $catalogs)
    {
    }

    public function lookup(User $user, MovieSource $source, string $id): MovieDetails
    {
        return $this->catalogs[$source->name]->find($id);
    }

    public function runtime(User $user, MovieSource $source, string $id): int
    {
        return $this->lookup($user, $source, $id)->runtimeMinutes;
    }
}
