<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E06MovieInfo;

/** Stabilny kontrakt sceny: kto pyta o film. Dane o filmach ogląda tylko personel kina. */
final readonly class User
{
    public function __construct(public string $name, public bool $staff)
    {
    }

    public static function staff(string $name): self
    {
        return new self($name, true);
    }

    public static function guest(string $name): self
    {
        return new self($name, false);
    }
}
