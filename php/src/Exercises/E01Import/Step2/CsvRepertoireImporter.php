<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

/** Krok 2: konkretny twórca - importer CSV mówi tylko, jaki parser tworzy i jak się podpisuje. */
final class CsvRepertoireImporter extends RepertoireImporter
{
    public function label(): string
    {
        return 'CSV';
    }

    protected function createLineParser(): LineParser
    {
        return new CsvLineParser();
    }
}
