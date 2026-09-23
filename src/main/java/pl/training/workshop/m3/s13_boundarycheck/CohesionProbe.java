package pl.training.workshop.m3.s13_boundarycheck;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Narzędzie sceny: prosta diagnostyka spójności w stylu LCOM4. Metody są połączone,
 * gdy używają wspólnego pola instancji albo jedna woła drugą. LCOM4 = liczba spójnych
 * grup metod; wynik większy niż 1 podpowiada, że w klasie mieszkają dwa pojęcia.
 * <p>To heurystyka na źródle w stylu tego repozytorium (pola private, typy bez spacji,
 * metody wcięte o 4 spacje) - sygnał do rozmowy, nie wyrocznia. Konstruktor pomijamy.
 */
public final class CohesionProbe {
    private static final String TYPE = "[\\w][\\w<>\\[\\],.?]*";
    private static final Pattern FIELD = Pattern.compile(
            "^ {4}private\\s+(?!static)(?:final\\s+)?" + TYPE + "\\s+(\\w+)\\s*(?:=.*)?;\\s*$");
    private static final Pattern METHOD = Pattern.compile(
            "^ {4}(?:public |private |protected )?(?:static )?" + TYPE + "\\s+(\\w+)\\s*\\(.*\\)\\s*\\{\\s*$");

    public record Result(int lcom4, List<String> groups) {
    }

    public Result analyze(Path sourceFile) {
        List<String> lines = read(sourceFile);
        String className = sourceFile.getFileName().toString().replace(".java", "");
        Set<String> fields = new LinkedHashSet<>();
        Map<String, String> bodies = new LinkedHashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            Matcher field = FIELD.matcher(lines.get(i));
            Matcher method = METHOD.matcher(lines.get(i));
            if (field.matches()) {
                fields.add(field.group(1));
            } else if (method.matches() && !method.group(1).equals(className)) {
                StringBuilder body = new StringBuilder();
                int depth = 0;
                int j = i;
                do {
                    String line = lines.get(j++);
                    depth += count(line, '{') - count(line, '}');
                    body.append(line).append('\n');
                } while (depth > 0 && j < lines.size());
                bodies.merge(method.group(1), body.toString(), String::concat);
                i = j - 1;
            }
        }
        return groups(fields, bodies);
    }

    private Result groups(Set<String> fields, Map<String, String> bodies) {
        List<String> methods = new ArrayList<>(bodies.keySet());
        Map<String, String> parent = new LinkedHashMap<>();
        methods.forEach(m -> parent.put(m, m));
        for (String a : methods) {
            for (String b : methods) {
                boolean connected = calls(bodies.get(a), b) || sharesField(fields, bodies.get(a), bodies.get(b));
                if (!a.equals(b) && connected) {
                    parent.put(find(parent, a), find(parent, b));
                }
            }
        }
        Map<String, Set<String>> components = new TreeMap<>();
        for (String m : methods) {
            components.computeIfAbsent(find(parent, m), k -> new TreeSet<>()).add(m);
        }
        List<String> groups = components.values().stream()
                .map(g -> String.join(", ", g)).sorted().toList();
        return new Result(groups.size(), groups);
    }

    private static boolean calls(String body, String method) {
        return Pattern.compile("\\b" + method + "\\s*\\(").matcher(body.substring(body.indexOf('{'))).find();
    }

    private static boolean sharesField(Set<String> fields, String a, String b) {
        return fields.stream().anyMatch(f -> uses(a, f) && uses(b, f));
    }

    private static boolean uses(String body, String field) {
        return Pattern.compile("\\b" + field + "\\b").matcher(body.substring(body.indexOf('{'))).find();
    }

    private static String find(Map<String, String> parent, String node) {
        String root = node;
        while (!parent.get(root).equals(root)) {
            root = parent.get(root);
        }
        return root;
    }

    private static int count(String line, char c) {
        return (int) line.chars().filter(ch -> ch == c).count();
    }

    private static List<String> read(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
