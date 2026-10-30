<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step1;

use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;
use Training\Workshop\Exercises\E06MovieInfo\User;

/** Krok 1: korzeń kompozycji - tu, a nie w serwisie, każde źródło dostaje swój adapter. */
final class MovieInfo
{
    private readonly MovieInfoService $service;

    public function __construct(OmdbLikeClient $omdb, LegacyMovieRows $legacy)
    {
        $this->service = new MovieInfoService([
            MovieSource::OMDB->name => new OmdbCatalogAdapter($omdb),
            MovieSource::LEGACY->name => new LegacyCatalogAdapter($legacy),
        ]);
    }

    public static function create(): self
    {
        return new self(new OmdbLikeClient(), new LegacyMovieRows());
    }

    public function lookup(User $user, MovieSource $source, string $id): MovieDetails
    {
        return $this->service->lookup($user, $source, $id);
    }

    public function runtime(User $user, MovieSource $source, string $id): int
    {
        return $this->service->runtime($user, $source, $id);
    }
}
