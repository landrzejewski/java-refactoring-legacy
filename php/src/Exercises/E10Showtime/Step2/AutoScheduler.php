<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step2;

use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/**
 * Krok 2: automat używa tej samej fasady co panel kasjera. Świadoma zmiana zachowania: seanse
 * startowane automatycznie trafiają teraz do raportu frekwencji (dokumentuje ją E10BehaviourChangeTest).
 */
final readonly class AutoScheduler
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
