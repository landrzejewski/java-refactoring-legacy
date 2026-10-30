<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

/** Krok 2: konkretny twórca - importer JSON Lines mówi tylko, jaki parser tworzy i jak się podpisuje. */
final class JsonLinesRepertoireImporter extends RepertoireImporter
{
    public function label(): string
    {
        return 'JSONL';
    }

    protected function createLineParser(): LineParser
    {
        return new JsonLineParser();
    }
}
