<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Start;

use Training\Workshop\Exercises\E06MovieInfo\LegacyMovieRows;
use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;
use Training\Workshop\Exercises\E06MovieInfo\MovieSource;
use Training\Workshop\Exercises\E06MovieInfo\OmdbLikeClient;
use Training\Workshop\Exercises\E06MovieInfo\User;

/** Start: punkt wejścia sceny - panel informacji o filmie w kasie. Przez niego woła test. */
final class MovieInfo
{
    private readonly MovieInfoService $service;

    public function __construct(OmdbLikeClient $omdb, LegacyMovieRows $legacy)
    {
        $this->service = new MovieInfoService($omdb, $legacy);
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
