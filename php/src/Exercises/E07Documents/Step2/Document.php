<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

/** Krok 2: wspólny kontrakt rdzenia dokumentu i każdej opcjonalnej sekcji, która go owija. */
interface Document
{
    public function render(): string;
}
