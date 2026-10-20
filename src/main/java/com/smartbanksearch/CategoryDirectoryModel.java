package com.smartbanksearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A read-only, offline directory of the same category aliases used by bank
 * search. There is no second editable catalogue to drift out of sync.
 */
public final class CategoryDirectoryModel
{
    public static final class Entry
    {
        public final String title;
        public final String primaryAlias;
        public final List<String> aliases;

        private Entry(String title, List<String> aliases)
        {
            this.title = title;
            this.aliases = Collections.unmodifiableList(new ArrayList<>(aliases));
            this.primaryAlias = this.aliases.stream()
                .min(Comparator.comparingInt(String::length).thenComparing(String::compareToIgnoreCase))
                .orElse("");
        }

        @Override
        public String toString()
        {
            return title + "  •  @" + primaryAlias;
        }

        /** Presentation only: never change the category's canonical title or aliases. */
        public String displayLabel(boolean favorite)
        {
            return (favorite ? "\u2605 " : "   ") + toString();
        }

        public boolean contains(String query)
        {
            String term = clean(query);
            if (term.isEmpty()) { return true; }
            if (clean(title).contains(term)) { return true; }
            for (String alias : aliases)
            {
                if (clean(alias).contains(term)) { return true; }
            }
            return false;
        }
    }

    private static String clean(String value)
    {
        if (value == null) { return ""; }
        String text = value.toLowerCase(Locale.ROOT).trim();
        if (text.startsWith("@")) { text = text.substring(1); }
        return text.replace('-', ' ').replace('_', ' ').replaceAll("\\s+", " ");
    }

    private final List<Entry> entries;

    public CategoryDirectoryModel()
    {
        List<Entry> all = new ArrayList<>();
        for (Map.Entry<String, List<String>> group : CategoryMatcher.catalog().entrySet())
        {
            if (!group.getValue().isEmpty())
            {
                all.add(new Entry(group.getKey(), group.getValue()));
            }
        }
        all.sort(Comparator.comparing(e -> e.title.toLowerCase(Locale.ROOT)));
        entries = Collections.unmodifiableList(all);
    }

    public int size()
    {
        return entries.size();
    }

    public List<Entry> filter(String text)
    {
        if (clean(text).isEmpty()) { return entries; }
        List<Entry> filtered = new ArrayList<>();
        for (Entry entry : entries)
        {
            if (entry.contains(text)) { filtered.add(entry); }
        }
        return filtered;
    }
}
