package com.kaiv.taglib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Replace {

    public static String replaceWithRegex(String inputField, String inputPattern) {
        String outputString = inputField;

        if (inputPattern.startsWith("&")) {
            inputPattern = inputPattern.split("&")[1];

            String result = checkAndHighlightMatches(inputField, inputPattern, true);
            if (!result.isEmpty()) {
                outputString = result;
            }


        } else if (inputPattern.contains("&")) {

            String[] searchWords = inputPattern.split("&");

            String tempString = "";

            for (String currentSearchWord : searchWords) {

                if (tempString.isEmpty()) {
                    String result = checkAndHighlightMatches(inputField, currentSearchWord, false);
                    if (!result.isEmpty()) {
                        tempString = result;
                    }
                } else {
                    String result = checkAndHighlightMatches(tempString, currentSearchWord, false);
                    if (!result.isEmpty()) {
                        tempString = result;
                    }
                }
            }

            if (!tempString.isEmpty()) {
                outputString = tempString;
            }

        } else {
            String result = checkAndHighlightMatches(inputField, inputPattern, false);
            if (!result.isEmpty()) {
                outputString = result;
            }
        }

        return outputString;
    }

    public static void main(String[] args) {
        System.out.println(checkAndHighlightMatches("Кастран Наталія Володимирівна", "^К.* \\b.+ \\bВ", true));
    }

    private static String checkAndHighlightMatches(String inputField, String inputPattern, boolean isNeedToUseRegex) {
        String outputString = "";

        if (inputPattern.startsWith("+")) {
            inputPattern = inputPattern.replaceAll("\\+", "\\\\+");
        }
        if (inputPattern.contains("(")) {
            inputPattern = inputPattern.replaceAll("\\(", "\\\\(");
        }
        if (inputPattern.contains(")")) {
            inputPattern = inputPattern.replaceAll("\\)", "\\\\)");
        }

        Pattern pattern = getPatternObject(inputPattern, isNeedToUseRegex);

        if (pattern != null) {

            Matcher matcher;
            if (isNeedToUseRegex) {
                matcher = pattern.matcher(inputField);
            } else {
                matcher = pattern.matcher(inputField.toLowerCase());
            }

            // String tempString = "";
            while (matcher.find()) {

                int startIndex = matcher.start();
                int endIndex = matcher.end();

                if (startIndex != endIndex) {
                    String foundedString = inputField.substring(startIndex, endIndex);
                    String replacedString = "<span class=highLight>" + foundedString + "</span>";

                  /*  if (foundedString.contains("+")) {
                        foundedString = foundedString.replace("+", "\\+");
                    }
                    if (foundedString.contains("(")) {
                        foundedString = foundedString.replaceAll("\\(", "\\\\(");
                    }
                    if (foundedString.contains(")")) {
                        foundedString = foundedString.replaceAll("\\)", "\\\\)");
                    }*/

                    try {

                        String firstPart = inputField.substring(0, startIndex);
                        String endPart = inputField.substring(endIndex, inputField.length());
                        String highlightedResult = firstPart + replacedString + endPart;

                        if (!highlightedResult.isEmpty()) {
                            outputString = highlightedResult;
                        }
                        //outputString = inputField.replace(foundedString, replacedString);

                       /* if (tempString.isEmpty()) {
                            String result = inputField.replaceAll(foundedString, replacedString);
                            if (!result.isEmpty()) {
                                tempString = result;
                            }
                        } else {
                            String result = tempString.replaceAll(foundedString, replacedString);
                            if (!result.isEmpty()) {
                                tempString = result;
                            }
                        }*/

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
           /* if (!tempString.isEmpty()) {
                outputString = tempString;
            }*/

        } else {
            outputString = inputField;
        }
        return outputString;
    }

    private static Pattern getPatternObject(String regex, boolean isNeedToUseRegex) {

        if (!isNeedToUseRegex) {
            regex = regex.toLowerCase();
        }

        Pattern pattern = null;
        try {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pattern;
    }
}
