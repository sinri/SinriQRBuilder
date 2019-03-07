package com.sinri;

import java.util.ArrayList;

class QRMath {

    private static ArrayList<Integer> QR_MATH_EXP_TABLE = null;
    private static ArrayList<Integer>  QR_MATH_LOG_TABLE = null;

    /**
     * Initialize the $QR_MATH_EXP_TABLE and $QR_MATH_LOG_TABLE
     * I had add checker before use them. No need to call this previously now.
     */
    private static void init()
    {

        QR_MATH_EXP_TABLE = createNumArray(256);

        for (int i = 0; i < 8; i++) {
            QR_MATH_EXP_TABLE.set(i, 1 << i);
        }

        for (int i = 8; i < 256; i++) {
            QR_MATH_EXP_TABLE.set(i,  QR_MATH_EXP_TABLE.get(i - 4)
                ^ QR_MATH_EXP_TABLE.get(i - 5)
                ^ QR_MATH_EXP_TABLE.get(i - 6)
                ^ QR_MATH_EXP_TABLE.get(i - 8)
            );
        }

        QR_MATH_LOG_TABLE = createNumArray(256);

        for (int i = 0; i < 255; i++) {
            QR_MATH_LOG_TABLE.set(QR_MATH_EXP_TABLE.get(i),  i);
        }
    }

    static ArrayList<Integer> createNumArray(int length)
    {
        ArrayList<Integer> num_array = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            num_array.add(0);
        }
        return num_array;
    }

    static Integer glog(int n) {

        if (n < 1) {
            //trigger_error("log(".json_encode($n).")", E_USER_ERROR);
            throw new RuntimeException("log(" + n + ") could not be computed");
        }

        if (QR_MATH_LOG_TABLE==null) {
            init();
        }
        return QR_MATH_LOG_TABLE.get(n);
    }

    static Integer gexp(int n)
    {
        if (QR_MATH_EXP_TABLE==null) {
            init();
        }

        while (n < 0) {
            n += 255;
        }

        while (n >= 256) {
            n -= 255;
        }

        return QR_MATH_EXP_TABLE.get(n);
    }
}
