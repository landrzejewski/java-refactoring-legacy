package pl.training.workshop.m3.s06_yagni.start;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Start: rejestr pluginów konfigurowany napisem "morning,vip" - "na przyszłość". */
public final class RuleRegistry {
    private final Map<String, Supplier<PricingRule>> plugins = new LinkedHashMap<>();

    public static RuleRegistry withDefaults() {
        RuleRegistry registry = new RuleRegistry();
        registry.register("morning", MorningRule::new);
        registry.register("vip", VipRule::new);
        return registry;
    }

    public void register(String name, Supplier<PricingRule> plugin) {
        plugins.put(name, plugin);
    }

    public List<PricingRule> resolve(String activeRules) {
        return Arrays.stream(activeRules.split(","))
                .map(String::trim)
                .map(this::create)
                .sorted(Comparator.comparingInt(PricingRule::priority))
                .toList();
    }

    private PricingRule create(String name) {
        Supplier<PricingRule> plugin = plugins.get(name);
        if (plugin == null) {
            throw new IllegalArgumentException("brak reguly: " + name);
        }
        return plugin.get();
    }
}
