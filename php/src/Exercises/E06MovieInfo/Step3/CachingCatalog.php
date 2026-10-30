<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo\Step3;

use Training\Workshop\Exercises\E06MovieInfo\MovieDetails;

/** Krok 3: pamięć podręczna jako zastępca katalogu - bez zmian. */
final class CachingCatalog implements MovieCatalog
{
    /** @var array<string, MovieDetails> */
    private array $cache = [];

    public function __construct(private readonly MovieCatalog $target)
    {
    }

    public function find(string $id): MovieDetails
    {
        $cached = $this->cache[$id] ?? null;
        if ($cached !== null) {
            return $cached;
        }
        $details = $this->target->find($id);
        $this->cache[$id] = $details;
        return $details;
    }
}
