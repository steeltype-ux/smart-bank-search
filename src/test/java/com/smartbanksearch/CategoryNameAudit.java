package com.smartbanksearch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Offline audit harness. TSV input: item-id TAB actual item name.
 * TSV output: category-title TAB item-id TAB actual name.
 * Uses the EXACT production predicates, not a Python imitation.
 * Equipment-only categories may undercount: item stats are unavailable here.
 */
public final class CategoryNameAudit
{
    private static final class Rule
    {
        final String title;
        final CategoryMatcher.Query parsed;

        Rule(String title, String alias)
        {
            this.title = title;
            // Focused boost categories use the unprefixed alias; @skill is deliberately broad.
            boolean focused = title.endsWith(" boosts") || title.equals("Prayer restores");
            parsed = CategoryMatcher.parse((focused ? "" : "@") + alias, true);
            if (!parsed.isCategory() || !parsed.getCategoryName().equals(title))
            {
                throw new IllegalStateException("Unresolved alias: " + alias + " (" + title + ")");
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            throw new IllegalArgumentException("Usage: CategoryNameAudit items.tsv matches.tsv");
        }
        List<Rule> rules = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : CategoryMatcher.catalog().entrySet())
        {
            if (entry.getValue().isEmpty())
            {
                throw new IllegalStateException("Category without an alias: " + entry.getKey());
            }
            rules.add(new Rule(entry.getKey(), entry.getValue().get(0)));
        }

        long itemCount = 0;
        long matchCount = 0;
        try (BufferedReader in = Files.newBufferedReader(Path.of(args[0]), StandardCharsets.UTF_8);
             BufferedWriter out = Files.newBufferedWriter(Path.of(args[1]), StandardCharsets.UTF_8))
        {
            out.write("category\tid\tname\n");
            String row;
            while ((row = in.readLine()) != null)
            {
                int tab = row.indexOf('\t');
                if (tab < 1) { continue; }
                String id = row.substring(0, tab);
                String name = row.substring(tab + 1);
                ItemFacts facts = ItemFacts.nameOnly(name);
                itemCount++;
                for (Rule rule : rules)
                {
                    if (rule.parsed.matches(facts))
                    {
                        out.write(rule.title);
                        out.write('\t');
                        out.write(id);
                        out.write('\t');
                        out.write(name);
                        out.write('\n');
                        matchCount++;
                    }
                }
            }
        }
        System.out.println("CATEGORY_NAME_AUDIT items=" + itemCount + " categories="
            + rules.size() + " matched_pairs=" + matchCount);
    }
}
