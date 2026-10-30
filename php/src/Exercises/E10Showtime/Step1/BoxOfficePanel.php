<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E10Showtime\Step1;

use Training\Workshop\Exercises\E10Showtime\ShowRequest;

/** Krok 1: panel kasjera sam zna kroki startu seansu i ich kolejność: bramka, sala, raport. */
final readonly class BoxOfficePanel
{
    public function __construct(
        private TicketScanner $scanner,
        private HallController $hall,
        private AttendanceReport $report,
    ) {
    }

    public function startShow(ShowRequest $request): void
    {
        $attendance = $this->scanner->close($request->screeningId);
        $this->hall->startShow($request->title);
        $this->report->record($request->screeningId, $attendance);
    }

    public function endShow(string $screeningId): void
    {
        $this->hall->endShow();
        $this->report->close($screeningId);
    }
}
