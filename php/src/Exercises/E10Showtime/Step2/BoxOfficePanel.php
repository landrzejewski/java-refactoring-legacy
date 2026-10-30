<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/** Krok 2: panel kasjera zna tylko fasadę - nie wie, z ilu kroków składa się start seansu. */
final readonly class BoxOfficePanel
{
    public function __construct(private Showtime $showtime)
    {
    }

    public function startShow(ShowRequest $request): void
    {
        $this->showtime->start($request);
    }

    public function endShow(string $screeningId): void
    {
        $this->showtime->end($screeningId);
    }
}
