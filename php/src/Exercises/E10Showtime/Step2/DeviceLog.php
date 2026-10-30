<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

/**
 * Krok 2: wspólny dziennik zdarzeń urządzeń i podsystemów - kolejność wpisów jest obserwowalnym zachowaniem sali.
 *
 * @internal podsystem ukryty za fasadą {@see Showtime} (PHP nie ma widoczności pakietowej)
 */
final class DeviceLog
{
    /** @var list<string> */
    private array $lines = [];

    public function add(string $line): void
    {
        $this->lines[] = $line;
    }

    public function dump(): string
    {
        return implode("\n", $this->lines);
    }
}
