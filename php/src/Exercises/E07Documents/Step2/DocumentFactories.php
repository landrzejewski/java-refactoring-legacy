<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E07Documents\Step2;

use Training\Workshop\Exercises\E07Documents\Channel;

/**
 * Krok 2: jedyny switch po kanale. Fabryki są bezstanowe, więc współdzielimy jedne instancje.
 * Nowy kanał to jedna nowa klasa fabryki i jeden wpis tutaj.
 */
final class DocumentFactories
{
    private function __construct()
    {
    }

    public static function forChannel(Channel $channel): DocumentFactory
    {
        static $kiosk = new KioskDocuments();
        static $mobile = new MobileDocuments();
        static $boxOffice = new BoxOfficeDocuments();
        return match ($channel) {
            Channel::KIOSK => $kiosk,
            Channel::MOBILE => $mobile,
            Channel::BOX_OFFICE => $boxOffice,
        };
    }
}
