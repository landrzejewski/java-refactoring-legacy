<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

use Training\Workshop\Exercises\E01Import\ImportRequest;

/** Krok 2: klient nie zna rozszerzeń ani formatów - pyta importer o podpis raportu. */
final class RepertoireImport
{
    public function load(ImportRequest $request): string
    {
        $importer = Importers::forFile($request->fileName);
        $entries = $importer->importLines($request->lines);
        $report = '[' . $importer->label() . '] pozycji: ' . count($entries);
        foreach ($entries as $entry) {
            $report .= "\n" . $entry;
        }
        return $report;
    }
}
