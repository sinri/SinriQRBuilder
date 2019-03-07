package com.sinri;

class QR8BitByte extends QRData {

    QR8BitByte(String data){
        super(QRModeEnum.QR_MODE_8BIT_BYTE,data);
    }

    @Override
    void write(QRBitBuffer buffer) {
        String data = getData();
        for (int i = 0; i < data.length(); i++) {
            buffer.put(QRUtil.ordAsPHP(data.charAt(i)), 8);
        }
    }
}
