package com.cartmind.util;

import com.cartmind.dto.ExtractedRequirements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FallbackParser {

    private FallbackParser() {
    }

    public static ExtractedRequirements parse(String query) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException(
                    "Search query cannot be empty"
            );
        }

        String text =
                query.trim();

        String lower =
                text.toLowerCase(Locale.ROOT);

        ExtractedRequirements result =
                new ExtractedRequirements();


        // ==========================================
        // LANGUAGE
        // ==========================================

        if (containsAny(
                lower,
                "gujarati"
        )) {

            result.setLanguage("Gujarati");

        } else if (containsAny(
                lower,
                "hindi"
        )) {

            result.setLanguage("Hindi");

        } else if (containsAny(
                lower,
                "english"
        )) {

            result.setLanguage("English");

        } else if (containsAny(
                lower,
                "hinglish",
                "bhai",
                "mujhe",
                "chahiye",
                "ke andar",
                "ke ander",
                "ke under",
                "wala",
                "wali"
        )) {

            result.setLanguage("Hinglish");

        }


        // ==========================================
        // CATEGORY
        // ==========================================

        if (containsAny(
                lower,
                "laptop",
                "notebook"
        )) {

            result.setCategory("laptop");

        } else if (containsAny(
                lower,
                "mobile",
                "smartphone",
                "phone"
        )) {

            result.setCategory("mobile");

        } else if (containsAny(
                lower,
                "tablet",
                "ipad"
        )) {

            result.setCategory("tablet");

        } else if (containsAny(
                lower,
                "monitor",
                "display"
        )) {

            result.setCategory("monitor");

        } else if (containsAny(
                lower,
                "headphone",
                "headphones",
                "earbuds",
                "earphone"
        )) {

            result.setCategory("audio");

        } else if (containsAny(
                lower,
                "keyboard"
        )) {

            result.setCategory("keyboard");

        } else if (containsAny(
                lower,
                "mouse"
        )) {

            result.setCategory("mouse");

        } else if (containsAny(
                lower,
                "food",
                "pizza",
                "burger",
                "meal"
        )) {

            result.setCategory("food");

        } else if (containsAny(
                lower,
                "grocery",
                "groceries"
        )) {

            result.setCategory("grocery");
        }


        // ==========================================
        // INTENT
        // ==========================================

        if (containsAny(
                lower,
                "coding",
                "programming",
                "developer",
                "software development",
                "development"
        )) {

            result.setIntent("coding");

        } else if (containsAny(
                lower,
                "gaming",
                "game",
                "games"
        )) {

            result.setIntent("gaming");

        } else if (containsAny(
                lower,
                "office",
                "work",
                "business",
                "office work"
        )) {

            result.setIntent("office");

        } else if (containsAny(
                lower,
                "student",
                "college",
                "study",
                "education"
        )) {

            result.setIntent("student");

        } else if (containsAny(
                lower,
                "photography",
                "camera",
                "photo"
        )) {

            result.setIntent("photography");
        }


        // ==========================================
        // MAXIMUM BUDGET
        // ==========================================

        BigDecimal maxBudget =
                extractMaxBudget(text);

        if (maxBudget != null) {

            result.setMaxBudget(
                    maxBudget
            );
        }


        // ==========================================
        // MINIMUM BUDGET
        // ==========================================

        BigDecimal minBudget =
                extractMinBudget(text);

        if (minBudget != null) {

            result.setMinBudget(
                    minBudget
            );
        }


        // ==========================================
        // RAM
        // ==========================================

        Integer ram =
                extractInteger(
                        text,
                        "(\\d+)\\s*(?:gb)?\\s*(?:ram|रैम)"
                );

        if (ram != null) {

            result.setMinRamGb(
                    ram
            );
        }


        // ==========================================
        // STORAGE
        // ==========================================

        Integer storage =
                extractStorageGb(text);

        if (storage != null) {

            result.setMinStorageGb(
                    storage
            );
        }


        // ==========================================
        // STORAGE TYPE
        // ==========================================

        if (containsAny(
                lower,
                "ssd",
                "nvme",
                "solid state"
        )) {

            result.setStorageType(
                    "SSD"
            );

        } else if (containsAny(
                lower,
                "hdd",
                "hard disk"
        )) {

            result.setStorageType(
                    "HDD"
            );
        }


        // ==========================================
        // BATTERY
        // ==========================================

        Integer battery =
                extractBatteryMah(text);

        if (battery != null) {

            result.setMinBatteryMah(
                    battery
            );
        }


        // ==========================================
        // RATING
        // ==========================================

        Double rating =
                extractRating(text);

        if (rating != null) {

            /*
             * ExtractedRequirements.setMinRating()
             * expects Double.
             */
            result.setMinRating(
                    rating
            );
        }


        // ==========================================
        // BRAND
        // ==========================================

        List<String> brands =
                extractBrands(lower);

        if (!brands.isEmpty()) {

            result.setBrands(
                    brands
            );
        }


        // ==========================================
        // PREFERRED FEATURES
        // ==========================================

        List<String> features =
                new ArrayList<>();

        addFeatureIfPresent(
                features,
                lower,
                "lightweight"
        );

        addFeatureIfPresent(
                features,
                lower,
                "light weight"
        );

        addFeatureIfPresent(
                features,
                lower,
                "backlit keyboard"
        );

        addFeatureIfPresent(
                features,
                lower,
                "backlit"
        );

        addFeatureIfPresent(
                features,
                lower,
                "fingerprint"
        );

        addFeatureIfPresent(
                features,
                lower,
                "oled"
        );

        addFeatureIfPresent(
                features,
                lower,
                "touchscreen"
        );

        addFeatureIfPresent(
                features,
                lower,
                "touch screen"
        );

        addFeatureIfPresent(
                features,
                lower,
                "5g"
        );

        addFeatureIfPresent(
                features,
                lower,
                "long battery"
        );

        addFeatureIfPresent(
                features,
                lower,
                "good battery"
        );

        if (!features.isEmpty()) {

            result.setPreferredFeatures(
                    features
            );
        }


        return result;
    }


    // ==========================================
    // MAXIMUM BUDGET
    //
    // Supports:
    //
    // 5000 ke andar
    // 5000 ke ander
    // 5000 ke under
    // 5000 tak
    // 5000 se kam
    // 5000 ke niche
    // 5000 ke neeche
    // ₹5000 ke andar
    // 50k ke andar
    // 1 lakh ke andar
    // under 5000
    // below 5000
    // within 5000
    // upto 5000
    // up to 5000
    // less than 5000
    // ==========================================

    private static BigDecimal extractMaxBudget(
            String text
    ) {

        String number =
                "(?:₹|rs\\.?|inr\\s*)?"
                        + "([0-9][0-9,]*(?:\\.\\d+)?)";

        /*
         * Example:
         *
         * under 5000
         * below ₹5000
         * within 50k
         * upto 1 lakh
         */

        Pattern beforeAmountPattern =
                Pattern.compile(
                        "(?:under|below|within|max(?:imum)?|upto|up to|less than)"
                                + "\\s*"
                                + number
                                + "\\s*(k|thousand|lakh|lac)?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher beforeMatcher =
                beforeAmountPattern.matcher(text);

        if (beforeMatcher.find()) {

            return normalizeAmount(
                    beforeMatcher.group(1),
                    beforeMatcher.group(2)
            );
        }


        /*
         * Example:
         *
         * 5000 ke andar
         * 5000 ke ander
         * 5000 ke under
         * 5000 tak
         * 5000 se kam
         * 5000 ke niche
         * 5000 ke neeche
         */

        Pattern afterAmountPattern =
                Pattern.compile(
                        "₹?\\s*"
                                + "([0-9][0-9,]*(?:\\.\\d+)?)"
                                + "\\s*(k|thousand|lakh|lac)?"
                                + "\\s*(?:ke\\s*)?"
                                + "(?:andar|ander|under|within|tak|se\\s*kam|niche|neeche)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher afterMatcher =
                afterAmountPattern.matcher(text);

        if (afterMatcher.find()) {

            return normalizeAmount(
                    afterMatcher.group(1),
                    afterMatcher.group(2)
            );
        }


        /*
         * Example:
         *
         * 5000 budget
         * 5000 ka budget
         */

        Pattern budgetPattern =
                Pattern.compile(
                        "₹?\\s*"
                                + "([0-9][0-9,]*(?:\\.\\d+)?)"
                                + "\\s*(k|thousand|lakh|lac)?"
                                + "\\s*(?:ka\\s+budget|budget)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher budgetMatcher =
                budgetPattern.matcher(text);

        if (budgetMatcher.find()) {

            return normalizeAmount(
                    budgetMatcher.group(1),
                    budgetMatcher.group(2)
            );
        }


        return null;
    }


    // ==========================================
    // MINIMUM BUDGET
    //
    // Supports:
    // above 30000
    // over 30000
    // more than 30000
    // minimum 30000
    // min 30000
    // ==========================================

    private static BigDecimal extractMinBudget(
            String text
    ) {

        String number =
                "(?:₹|rs\\.?|inr\\s*)?"
                        + "([0-9][0-9,]*(?:\\.\\d+)?)";

        Pattern pattern =
                Pattern.compile(
                        "(?:above|over|more than|minimum|min)"
                                + "\\s*"
                                + number
                                + "\\s*(k|thousand|lakh|lac)?",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            return normalizeAmount(
                    matcher.group(1),
                    matcher.group(2)
            );
        }

        return null;
    }


    // ==========================================
    // RAM EXTRACTION
    // ==========================================

    private static Integer extractInteger(
            String text,
            String regex
    ) {

        Pattern pattern =
                Pattern.compile(
                        regex,
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            return Integer.valueOf(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ==========================================
    // STORAGE EXTRACTION
    //
    // Important:
    // 16GB RAM should NOT be mistaken as
    // 16GB storage.
    // ==========================================

    private static Integer extractStorageGb(
            String text
    ) {

        /*
         * First look for an explicit storage unit.
         *
         * 512GB SSD
         * 1TB SSD
         * 512GB storage
         * 1TB ROM
         */

        Pattern explicitPattern =
                Pattern.compile(
                        "(\\d+)\\s*(gb|tb)"
                                + "\\s*(?:ssd|hdd|storage|rom|nvme)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher explicitMatcher =
                explicitPattern.matcher(text);

        if (explicitMatcher.find()) {

            return convertStorage(
                    explicitMatcher.group(1),
                    explicitMatcher.group(2)
            );
        }


        /*
         * Reverse form:
         *
         * SSD 512GB
         * storage 512GB
         */

        Pattern reversePattern =
                Pattern.compile(
                        "(?:ssd|hdd|storage|rom|nvme)"
                                + "\\s*(?:of)?\\s*"
                                + "(\\d+)\\s*(gb|tb)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher reverseMatcher =
                reversePattern.matcher(text);

        if (reverseMatcher.find()) {

            return convertStorage(
                    reverseMatcher.group(1),
                    reverseMatcher.group(2)
            );
        }


        /*
         * Final fallback:
         *
         * Search all GB/TB values.
         *
         * Skip values immediately followed by RAM.
         */

        Pattern genericPattern =
                Pattern.compile(
                        "(\\d+)\\s*(gb|tb)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher genericMatcher =
                genericPattern.matcher(text);

        while (genericMatcher.find()) {

            int end =
                    genericMatcher.end();

            String remaining =
                    text.substring(
                            end,
                            Math.min(
                                    text.length(),
                                    end + 12
                            )
                    ).toLowerCase(
                            Locale.ROOT
                    );


            /*
             * 16GB RAM → skip it.
             */

            if (
                    remaining.matches(
                            "\\s*ram.*"
                    )
            ) {

                continue;
            }


            return convertStorage(
                    genericMatcher.group(1),
                    genericMatcher.group(2)
            );
        }


        return null;
    }


    // ==========================================
    // STORAGE CONVERSION
    // ==========================================

    private static Integer convertStorage(
            String number,
            String unit
    ) {

        int value =
                Integer.parseInt(
                        number
                );

        if (
                unit.equalsIgnoreCase("tb")
        ) {

            value =
                    value * 1024;
        }

        return value;
    }


    // ==========================================
    // BATTERY
    // ==========================================

    private static Integer extractBatteryMah(
            String text
    ) {

        Pattern pattern =
                Pattern.compile(
                        "(\\d+)\\s*mah",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            return Integer.valueOf(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ==========================================
    // RATING
    //
    // Supports:
    // rating 4.5
    // rating above 4
    // rated 4.5
    // minimum rating 4
    // ==========================================

    private static Double extractRating(
            String text
    ) {

        Pattern pattern =
                Pattern.compile(
                        "(?:rating|rated)"
                                + "\\s*"
                                + "(?:of|above|over|at least|minimum|min)?"
                                + "\\s*"
                                + "([0-5](?:\\.\\d+)?)",
                        Pattern.CASE_INSENSITIVE
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {

            return Double.parseDouble(
                    matcher.group(1)
            );
        }

        return null;
    }


    // ==========================================
    // BRAND EXTRACTION
    // ==========================================

    private static List<String> extractBrands(
            String text
    ) {

        List<String> brands =
                new ArrayList<>();


        String[][] brandMap = {

                {"apple", "Apple"},
                {"samsung", "Samsung"},
                {"dell", "Dell"},
                {"hp", "HP"},
                {"lenovo", "Lenovo"},
                {"asus", "ASUS"},
                {"acer", "Acer"},
                {"msi", "MSI"},
                {"oneplus", "OnePlus"},
                {"oppo", "OPPO"},
                {"xiaomi", "Xiaomi"},
                {"redmi", "Redmi"},
                {"realme", "Realme"},
                {"vivo", "Vivo"},
                {"motorola", "Motorola"}

        };


        for (String[] brand :
                brandMap) {

            if (
                    text.contains(
                            brand[0]
                    )
            ) {

                brands.add(
                        brand[1]
                );
            }
        }


        return brands;
    }


    // ==========================================
    // AMOUNT NORMALIZATION
    // ==========================================

    private static BigDecimal normalizeAmount(
            String number,
            String unit
    ) {

        String clean =
                number.replace(
                        ",",
                        ""
                ).trim();


        BigDecimal amount =
                new BigDecimal(
                        clean
                );


        if (unit == null) {

            return amount;
        }


        String normalizedUnit =
                unit.toLowerCase(
                        Locale.ROOT
                );


        if (
                "k".equals(normalizedUnit)
                        || "thousand".equals(normalizedUnit)
        ) {

            return amount.multiply(
                    BigDecimal.valueOf(
                            1_000
                    )
            );
        }


        if (
                "lakh".equals(normalizedUnit)
                        || "lac".equals(normalizedUnit)
        ) {

            return amount.multiply(
                    BigDecimal.valueOf(
                            100_000
                    )
            );
        }


        return amount;
    }


    // ==========================================
    // CONTAINS ANY
    // ==========================================

    private static boolean containsAny(
            String text,
            String... values
    ) {

        for (String value :
                values) {

            if (
                    text.contains(
                            value.toLowerCase(
                                    Locale.ROOT
                            )
                    )
            ) {

                return true;
            }
        }


        return false;
    }


    // ==========================================
    // FEATURE HELPER
    // ==========================================

    private static void addFeatureIfPresent(
            List<String> features,
            String text,
            String feature
    ) {

        if (
                text.contains(
                        feature.toLowerCase(
                                Locale.ROOT
                        )
                )
        ) {

            if (!features.contains(feature)) {

                features.add(
                        feature
                );
            }
        }
    }

}