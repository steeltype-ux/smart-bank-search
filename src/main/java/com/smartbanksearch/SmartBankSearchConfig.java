package com.smartbanksearch;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("smartbanksearch")
public interface SmartBankSearchConfig extends Config
{
    @ConfigItem(keyName = "enabled", name = "Enable smart search",
        description = "Add semantic category matches to the native bank search", position = 0)
    default boolean enabled() { return true; }

    @ConfigItem(keyName = "strict", name = "Full category phrases only",
        description = "Avoid ambiguous partial matches. Recommended ON. e.g. 'herb' and 'ranged gear'", position = 1)
    default boolean strict() { return true; }

    @ConfigItem(keyName = "diagnostics", name = "Diagnostic logging",
        description = "Log the first 25 checked items per category search. Only enable when troubleshooting.", position = 2)
    default boolean diagnostics() { return false; }
}
