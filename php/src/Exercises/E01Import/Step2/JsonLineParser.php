<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E01Import\Step2;

use InvalidArgumentException;

/** Krok 2: parser jednego obiektu JSON z wiersza pliku JSON Lines, komentarze od {@code //}. */
final class JsonLineParser implements LineParser
{
    private const string JSON_FIELD = '/"(\w+)"\s*:\s*"([^"]*)"/';

    public function isComment(string $line): bool
    {
        return str_starts_with($line, '//');
    }

    public function parse(string $line, int $number): RawScreening
    {
        if (!str_starts_with($line, '{') || !str_ends_with($line, '}')) {
            throw new InvalidArgumentException('line ' . $number . ': not a JSON object');
        }
        $fields = [];
        preg_match_all(self::JSON_FIELD, $line, $matches, PREG_SET_ORDER);
        foreach ($matches as $match) {
            $fields[$match[1]] = $match[2];
        }
        foreach (['title', 'format', 'hall', 'start'] as $required) {
            if (!array_key_exists($required, $fields)) {
                throw new InvalidArgumentException('line ' . $number . ': missing field: ' . $required);
            }
        }
        return new RawScreening($fields['title'], $fields['format'], $fields['hall'], $fields['start']);
    }
}
