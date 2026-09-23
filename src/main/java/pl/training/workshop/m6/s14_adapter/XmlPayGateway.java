package pl.training.workshop.m6.s14_adapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * "Biblioteka" starej bramki (nie zmieniamy jej): XML jako tekst, kwota w groszach.
 * Deterministyczna symulacja: powyżej 500.00 odmowa z kodem 51.
 */
public class XmlPayGateway {
    private static final Pattern CHARGE = Pattern.compile("<charge ref='([^']+)' amount='(\\d+)'/>");

    public String submit(String xml) {
        Matcher matcher = CHARGE.matcher(xml);
        if (!matcher.matches()) {
            return "<result status='ERROR' code='XML'/>";
        }
        if (Long.parseLong(matcher.group(2)) > 50_000) {
            return "<result status='DECLINED' code='51'/>";
        }
        return "<result status='OK' id='X-" + matcher.group(1) + "'/>";
    }
}
