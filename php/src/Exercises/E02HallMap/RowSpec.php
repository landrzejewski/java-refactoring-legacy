<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap;

/**
 * Stabilny kontrakt sceny: rząd o etykiecie (np. "A") i układzie miejsc, np. {@code "NN_VV"}.
 * Numer miejsca to pozycja w układzie liczona od 1, więc luka {@code _} zużywa numer.
 */
final readonly class RowSpec
{
    public function __construct(public string $label, public string $layout)
    {
    }
}
