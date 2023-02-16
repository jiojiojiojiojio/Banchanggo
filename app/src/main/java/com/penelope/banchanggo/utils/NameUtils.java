package com.penelope.banchanggo.utils;

import com.penelope.banchanggo.data.PostFilter;

public class NameUtils {

    public static String getPostFilterName(PostFilter filter) {
        switch (filter) {
            case RECENT: return "최신순";
            case LOW_PRICE: return "가격 낮은순";
            case HIGH_PRICE: return "가격 높은순";
        }
        return "";
    }

}
