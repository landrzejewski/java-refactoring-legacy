<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

/**
 * Krok 2: produkt metody wytwórczej - rozbiór jednego wiersza danego formatu. Parser ma własne życie:
 * można go przetestować bez importera i użyć np. w podglądzie wiersza w edytorze repertuaru.
 */
interface LineParser
{
    public function isComment(string $line): bool;

    public function parse(string $line, int $number): RawScreening;
}
