package com.xxx.BottonsBehavior;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Recommend {
    public static String recommendBestCombination(List<String> formatLines) {
        String bestVideo = null;
        String bestAudio = null;

        Pattern videoPattern = Pattern.compile("^\\d+.*?video.*?\\b(mp4|mkv)\\b");
        Pattern audioPattern = Pattern.compile("^\\d+.*?audio.*?\\b(m4a|aac)\\b");

        for (String line : formatLines) {
            if (bestVideo == null) {
                Matcher videoMatcher = videoPattern.matcher(line);
                if (videoMatcher.find()) {
                    bestVideo = line.split("\\s+")[0]; // 提取代碼
                }
            }
            if (bestAudio == null) {
                Matcher audioMatcher = audioPattern.matcher(line);
                if (audioMatcher.find()) {
                    bestAudio = line.split("\\s+")[0]; // 提取代碼
                }
            }
            if (bestVideo != null && bestAudio != null) {
                break;
            }
        }

        if (bestVideo != null && bestAudio != null) {
            return bestVideo + "+" + bestAudio;
        }
        return null;
    }
}
