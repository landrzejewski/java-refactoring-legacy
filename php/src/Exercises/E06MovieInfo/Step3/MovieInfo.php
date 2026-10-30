<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Krok 3: korzeń kompozycji decyduje o cyklu życia tabeli - jawnie przekazuje jedyną instancję.
 * Łańcuch zastępców jak w kroku 2: strażnik na zewnątrz, pamięć podręczna przed adapterem.
 */
final class MovieInfo
{
    private readonly MovieLookup $lookup;

    public function __construct(OmdbLikeClient $omdb, LegacyMovieRows $legacy)
    {
        $ratings = AgeRatingTable::INSTANCE;
        $this->lookup = new AuthorizingLookup(new MovieInfoService([
            MovieSource::OMDB->name => new CachingCatalog(new OmdbCatalogAdapter($omdb, $ratings)),
            MovieSource::LEGACY->name => new CachingCatalog(new LegacyCatalogAdapter($legacy, $ratings)),
        ]));
    }

    public static function create(): self
    {
        return new self(new OmdbLikeClient(), new LegacyMovieRows());
    }

    public function lookup(User $user, MovieSource $source, string $id): MovieDetails
    {
        return $this->lookup->lookup($user, $source, $id);
    }

    public function runtime(User $user, MovieSource $source, string $id): int
    {
        return $this->lookup->runtime($user, $source, $id);
    }
}
