package com.wenda.util;

public class RedisKeyUtil {

    private static String SPLIT = ":";
    private static String BIZ_LIKE = "UP";
    private static String BIZ_DISLIKE = "DOWN";
    private static String BIZ_EVENTQUEUE = "EVENT_QUEUE";

    public static String getLikeKey(int entityType, int entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityType, int entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENTQUEUE;
    }
}
