package com.sinri;

class QRKanji extends QRData {

    QRKanji(String data){
        super(QRModeEnum.QR_MODE_KANJI,data);
    }

    @Override
    void write(QRBitBuffer buffer) {
        String data = getData();

        int i = 0;

        while (i + 1 < data.length()) {

            int c = ((0xff & QRUtil.ordAsPHP(data.charAt(i)) << 8) | (0xff & QRUtil.ordAsPHP(data.charAt(i + 1))));

            if (0x8140 <= c && c <= 0x9FFC) {
                c -= 0x8140;
            } else if (0xE040 <= c && c <= 0xEBBF) {
                c -= 0xC140;
            } else {
                //trigger_error("illegal char at " . ($i + 1) . "/$c", E_USER_ERROR);
                throw new RuntimeException("illegal char at "+ (i + 1) + "/"+c);
            }

            c = ((c >> 8) & 0xff) * 0xC0 + (c & 0xff);

            buffer.put(c, 13);

            i += 2;
        }

        if (i < data.length()) {
            //trigger_error("illegal char at " . ($i + 1), E_USER_ERROR);
            throw new RuntimeException("illegal char at " + (i + 1));
        }
    }

    int getLength()
    {
        return (int)Math.floor((getData().length()) *1.0/ 2);
    }
}
