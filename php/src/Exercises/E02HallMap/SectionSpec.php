<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap;

/** Stabilny kontrakt sceny: sektor sali, np. "Parter" albo "Balkon", z listą rzędów. */
final readonly class SectionSpec
{
    /** @param list<RowSpec> $rows */
    public function __construct(public string $name, public array $rows)
    {
    }
}
