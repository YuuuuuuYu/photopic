package com.swyp8team2.common.util;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberPath;

import java.util.List;

public abstract class QueryDslUtil {

    public static Predicate ltCursor(Long cursor, NumberPath<Long> id) {
        return cursor == null ? null : id.lt(cursor);
    }

    public static boolean removeLastIfHasNext(int size, List<?> content) {
        if (content.size() > size) {
            content.remove(size);
            return true;
        }
        return false;
    }
}
