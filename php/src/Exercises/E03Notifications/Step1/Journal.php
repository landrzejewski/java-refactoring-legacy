<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E03Notifications\Step1;

/**
 * Krok 1: bramki wysylkowe kina (e-mail, SMS, push) i dziennik repertuaru w jednym miejscu.
 * To "urzadzenia" sceny - nie refaktoryzujemy ich, test czyta z nich kolejnosc efektow.
 */
final class Journal
{
    /** @var list<string> */
    private array $lines = [];

    public function email(?string $to, string $subject, string $body): void
    {
        $this->lines[] = 'EMAIL ' . $to . ' | ' . $subject . ' | ' . $body;
    }

    public function sms(?string $number, string $text): void
    {
        $this->lines[] = 'SMS ' . $number . ' | ' . $text;
    }

    public function push(?string $token, string $title, string $body): void
    {
        $this->lines[] = 'PUSH ' . $token . ' | ' . $title . ' | ' . $body;
    }

    public function note(string $line): void
    {
        $this->lines[] = $line;
    }

    /** @return list<string> */
    public function lines(): array
    {
        return $this->lines;
    }
}
