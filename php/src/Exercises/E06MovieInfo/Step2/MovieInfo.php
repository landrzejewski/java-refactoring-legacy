<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step2;

use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;
use Training\Workshop\Exercises\E06MovieInfo\User;

/**
 * Krok 2: korzeń kompozycji składa łańcuch zastępców. Kolejność jest zachowaniem: strażnik na
 * zewnątrz, żeby gość nie trafił nawet do pamięci podręcznej; pamięć tuż przed adapterem,
 * żeby zapamiętywać już przetłumaczone dane.
 */
final class MovieInfo
{
    private readonly MovieLookup $lookup;

    public function __construct(OmdbLikeClient $omdb, LegacyMovieRows $legacy)
    {
        $this->lookup = new AuthorizingLookup(new MovieInfoService([
            MovieSource::OMDB->name => new CachingCatalog(new OmdbCatalogAdapter($omdb)),
            MovieSource::LEGACY->name => new CachingCatalog(new LegacyCatalogAdapter($legacy)),
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
