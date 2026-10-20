package com.smartbanksearch;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class CategoryDirectoryModelTest
{
    @Test public void usesAllRegisteredCategoriesAndSearchesBothAliasesAndTitles()
    {
        CategoryDirectoryModel model = new CategoryDirectoryModel();
        assertEquals(CategoryMatcher.catalog().size(), model.size());
        assertTrue(model.size() >= 212);
        assertEquals(model.size(), model.filter("").size());

        assertEquals(1, model.filter("@gotr").size());
        assertEquals("Guardians of the Rift kit", model.filter("@gotr").get(0).title);
        assertEquals(1, model.filter("guardians of the rift").size());
        assertEquals(1, model.filter("cerberus").size());
        assertEquals(1, model.filter("@cerb").size());
        assertEquals(1, model.filter("@chinchompa hunting").size());
        assertEquals(1, model.filter("@cannon tasks").size());
        // The directory uses substring search: "attack" intentionally matches
        // additional headings or aliases such as attack-related gear.
        assertTrue(model.filter("@attack").stream()
            .anyMatch(entry -> entry.title.equals("Attack skill")));

        List<CategoryDirectoryModel.Entry> herbMatches = model.filter("herb");
        assertTrue(herbMatches.size() > 3);
        assertEquals(0, model.filter("this-is-not-a-real-category-12345").size());
    }

    @Test public void favoriteStarIsVisibleWithoutChangingCategorySearchData()
    {
        CategoryDirectoryModel model = new CategoryDirectoryModel();
        CategoryDirectoryModel.Entry gotr = model.filter("@gotr").get(0);

        String normalLabel = gotr.displayLabel(false);
        String favoriteLabel = gotr.displayLabel(true);
        assertTrue("Favorited category must show a star", favoriteLabel.startsWith("\u2605 "));
        assertFalse("Non-favorite category must not show a star", normalLabel.contains("\u2605"));
        assertTrue(favoriteLabel.endsWith(gotr.toString()));
        assertTrue(normalLabel.endsWith(gotr.toString()));

        // Decoration must not change canonical data or category matching.
        assertEquals("Guardians of the Rift kit", gotr.title);
        assertTrue(gotr.contains("@gotr"));
        assertEquals(1, model.filter("@gotr").size());
    }

    @Test public void primaryAliasAlwaysResolvesToDisplayedCategory()
    {
        CategoryDirectoryModel model = new CategoryDirectoryModel();
        for (CategoryDirectoryModel.Entry entry : model.filter(""))
        {
            assertFalse(entry.aliases.isEmpty());
            boolean focused = entry.title.endsWith(" boosts") || entry.title.equals("Prayer restores");
            String search = focused ? entry.primaryAlias : "@" + entry.primaryAlias;
            assertEquals(entry.title,
                CategoryMatcher.parse(search, true).getCategoryName());
            for (String alias : entry.aliases)
            {
                assertTrue("Missing searchable alias: " + alias, entry.contains(alias));
            }
        }
    }
}
