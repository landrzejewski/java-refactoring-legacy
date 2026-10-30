<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import;

/**
 * Stabilny kontrakt ćwiczenia: nazwa pliku z repertuarem (rozszerzenie mówi o formacie:
 * {@code .csv} albo {@code .jsonl}) i jego wiersze, już wczytane do pamięci.
 */
final readonly class ImportRequest
{
    /** @param list<string> $lines */
    public function __construct(public string $fileName, public array $lines)
    {
    }
}
