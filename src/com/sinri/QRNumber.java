package com.sinri;

class QRNumber extends QRData {

    QRNumber(String data){
        super(QRModeEnum.QR_MODE_NUMBER,data);
    }

    @Override
    void write(QRBitBuffer buffer) {
        String data = getData();

        int i = 0;

        while (i + 2 < data.length()) {
            int num = parseInt(data.substring(i,3));
            buffer.put(num, 10);
            i += 3;
        }

        if (i < data.length()) {

            if (data.length() - i == 1) {
                int num = parseInt(data.substring(i,i+1));
                buffer.put(num, 4);
            } else if (data.length() - i == 2) {
                int num = parseInt(data.substring(i,i+2));
                buffer.put(num, 7);
            }
        }
    }

    private static int parseInt(String s) {
        try {
            int num = 0;
            for (int i = 0; i < s.length(); i++) {
                num = num * 10 + parseIntAt(QRUtil.ordAsPHP(s.charAt(i)));
            }
            return num;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static int parseIntAt(int c) throws Exception {
        if (QRUtil.toCharCode("0") <= c && c <= QRUtil.toCharCode("9")) {
            return c - QRUtil.toCharCode("0");
        }

        //trigger_error("illegal char : $c", E_USER_ERROR);
        throw new Exception("illegal char : " + c);
    }
}
