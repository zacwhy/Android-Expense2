package com.example.expense.helpers;

import java.text.DecimalFormat;

public final class FormatHelper {

    public static String formatMoney(Object object) {
        return new DecimalFormat("0.00").format(object);
    }
    
}
