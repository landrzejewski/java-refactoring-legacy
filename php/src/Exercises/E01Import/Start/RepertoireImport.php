<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E01Import\ImportRequest;

/**
 * Start: klient importu - nocne zadanie wczytuje repertuar z pliku dystrybutora i drukuje raport.
 * Klient sam rozpoznaje format po rozszerzeniu, żeby podpisać raport; to samo rozpoznanie
 * robi jeszcze raz importer.
 */
final class RepertoireImport
{
    private RepertoireImporter $importer;

    public function __construct()
    {
        $this->importer = new RepertoireImporter();
    }

    public function load(ImportRequest $request): string
    {
        if (str_ends_with($request->fileName, '.csv')) {
            $label = 'CSV';
        } elseif (str_ends_with($request->fileName, '.jsonl')) {
            $label = 'JSONL';
        } else {
            throw new InvalidArgumentException('unsupported file: ' . $request->fileName);
        }
        $entries = $this->importer->importFile($request->fileName, $request->lines);
        $report = '[' . $label . '] pozycji: ' . count($entries);
        foreach ($entries as $entry) {
            $report .= "\n" . $entry;
        }
        return $report;
    }
}
