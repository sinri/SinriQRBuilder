package com.sinri;

class QRAlphaNum extends QRData {

    QRAlphaNum(String data){
        super(QRModeEnum.QR_MODE_ALPHA_NUM,data);
    }


    @Override
    void write(QRBitBuffer buffer) {
        int i = 0;
        String c = getData();

        while (i + 1 < c.length()) {
            buffer.put(getCode((char)QRUtil.ordAsPHP(c.charAt(i))) * 45 + getCode((char)QRUtil.ordAsPHP(c.charAt(i+1))), 11);
            i += 2;
        }

        if (i < c.length()) {
            buffer.put(getCode((char)QRUtil.ordAsPHP(c.charAt(i))), 6);
        }
    }

    private static int getCode(char c) {
        if (QRUtil.toCharCode("0") <= c && c <= QRUtil.toCharCode("9")) {
            return c - QRUtil.toCharCode("0");
        } else if (QRUtil.toCharCode("A") <= c
                && c <= QRUtil.toCharCode("Z")) {
            return c - QRUtil.toCharCode("A") + 10;
        } else {
            if (c == QRUtil.toCharCode(" ")) {
                return 36;
            } else if (c == QRUtil.toCharCode("$")) {
                return 37;
            } else if (c == QRUtil.toCharCode("%")) {
                return 38;
            } else if (c == QRUtil.toCharCode("*")) {
                return 39;
            } else if (c == QRUtil.toCharCode("+")) {
                return 40;
            } else if (c == QRUtil.toCharCode("-")) {
                return 41;
            } else if (c == QRUtil.toCharCode(".")) {
                return 42;
            } else if (c == QRUtil.toCharCode("/")) {
                return 43;
            } else if (c == QRUtil.toCharCode(":")) {
                return 44;
            }
            throw new RuntimeException("illegal char : " + c);
        }
    }
}
