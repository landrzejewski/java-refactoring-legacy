<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/**
 * Krok 1: automat startujący seanse według harmonogramu - kopia orkiestracji z panelu kasjera.
 * Kopia powstała, zanim doszedł raport frekwencji, i nikt jej nie uzupełnił: seanse startowane
 * automatycznie nie trafiają do raportu dla dystrybutora.
 */
final readonly class AutoScheduler
{
    public function __construct(
        private TicketScanner $scanner,
        private HallController $hall,
        private AttendanceReport $report,
    ) {
    }

    public function startShow(ShowRequest $request): void
    {
        $this->scanner->close($request->screeningId);
        $this->hall->startShow($request->title);
    }

    public function endShow(string $screeningId): void
    {
        $this->hall->endShow();
        $this->report->close($screeningId);
    }
}
