<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

/** Krok 1: wspólny dziennik zdarzeń urządzeń i podsystemów - kolejność wpisów jest obserwowalnym zachowaniem sali. */
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
