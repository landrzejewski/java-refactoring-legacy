<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents;

/** Wejście testu równoważności: co, dla jakiego kanału i z jakimi sekcjami drukujemy. */
final readonly class PrintRequest
{
    public function __construct(public ?Sale $sale, public ?Channel $channel, public ?PrintOptions $options)
    {
    }
}
