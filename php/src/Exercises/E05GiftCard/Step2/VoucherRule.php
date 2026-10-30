<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E05GiftCard\Step2;

use Training\Workshop\Exercises\E05GiftCard\ScreeningFacts;

/**
 * Krok 2: wyrażenie reguły vouchera. Każdy węzeł umie się ocenić dla faktów o seansie
 * i opisać dla kasjera, więc obie operacje chodzą po tym samym drzewie zamiast po dwóch
 * niezależnych rozbiorach tekstu.
 */
interface VoucherRule
{
    public function applies(ScreeningFacts $facts): bool;

    public function describe(): string;
}
