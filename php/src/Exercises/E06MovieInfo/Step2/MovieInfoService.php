<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Krok 2: prawdziwy podmiot. Serwis robi już tylko swoje: wybiera katalog dla źródła. Nie zna
 * uprawnień (parametr {@code $user} jest częścią wspólnego kontraktu, używa go strażnik) ani
 * pamięci podręcznej - obie odpowiedzialności siedzą w zastępcach o tych samych interfejsach.
 */
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
