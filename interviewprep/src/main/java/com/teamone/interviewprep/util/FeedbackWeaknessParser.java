package com.teamone.interviewprep.util;

import com.teamone.interviewprep.enums.WeaknessCategory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class FeedbackWeaknessParser {

    public Set<WeaknessCategory> parseCategories(String weaknessesText) {
        Set<WeaknessCategory> categories = new LinkedHashSet<>();

        if (weaknessesText == null || weaknessesText.isBlank()) {
            categories.add(WeaknessCategory.GENERAL);
            return categories;
        }

        String text = weaknessesText.toLowerCase(Locale.ROOT);

        if (containsAny(text, "o(n^2)", "o(n2)", "time complexity", "inefficient", "brute force", "optimal o(n)")) {
            categories.add(WeaknessCategory.TIME_COMPLEXITY);
        }

        if (containsAny(text, "space complexity", "memory usage", "extra space")) {
            categories.add(WeaknessCategory.SPACE_COMPLEXITY);
        }

        if (containsAny(text, "edge case", "null", "empty", "boundary", "duplicate", "invalid input")) {
            categories.add(WeaknessCategory.EDGE_CASES);
        }

        if (containsAny(text, "input validation", "null input", "empty input", "handle null", "handle empty")) {
            categories.add(WeaknessCategory.INPUT_VALIDATION);
        }

        if (containsAny(text, "incorrect", "partially correct", "does not solve", "wrong", "bug")) {
            categories.add(WeaknessCategory.CORRECTNESS);
        }

        if (containsAny(text, "readability", "formatting", "indentation", "one line", "line breaks")) {
            categories.add(WeaknessCategory.READABILITY);
        }

        if (containsAny(text, "code style", "naming", "comments", "clean code")) {
            categories.add(WeaknessCategory.CODE_STYLE);
        }

        if (containsAny(text, "star", "situation", "task", "action", "result")) {
            categories.add(WeaknessCategory.STAR_STRUCTURE);
        }

        if (containsAny(text, "clarity", "unclear", "vague", "generic")) {
            categories.add(WeaknessCategory.CLARITY);
        }

        if (containsAny(text, "ownership", "\"we\" vs \"i\"", "accountability")) {
            categories.add(WeaknessCategory.OWNERSHIP);
        }

        if (containsAny(text, "impact", "result", "measurable", "outcome")) {
            categories.add(WeaknessCategory.IMPACT);
        }

        if (containsAny(text, "relevance", "not relevant", "off topic")) {
            categories.add(WeaknessCategory.RELEVANCE);
        }

        if (categories.isEmpty()) {
            categories.add(WeaknessCategory.GENERAL);
        }

        return categories;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}