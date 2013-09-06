package com.example.common.helpers;

import java.text.DecimalFormat;

public final class FormatHelper {
    
    private FormatHelper() {}

    public static String formatMoney(Object object) {
        return new DecimalFormat("0.00").format(object);
    }
    
}
