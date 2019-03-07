package com.sinri;

public enum QRModeEnum {
    QR_MODE_AUTO_DETECT(0),
    QR_MODE_NUMBER(1),
    QR_MODE_ALPHA_NUM(1<<1),
    QR_MODE_8BIT_BYTE(1<<2),
    QR_MODE_KANJI(1<<3);

    public int getModValue() {
        return modValue;
    }

    public void setModValue(int modValue) {
        this.modValue = modValue;
    }

    private int modValue;

    QRModeEnum(int modeValue){
        this.modValue=modeValue;
    }
}
