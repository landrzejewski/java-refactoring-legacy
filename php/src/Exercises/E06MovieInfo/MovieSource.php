<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

/** Stabilny kontrakt sceny: skąd brać dane o filmie - z internetowej bazy albo ze starej bazy kina. */
enum MovieSource
{
    case OMDB;
    case LEGACY;
}
