package com.sinri;

public enum QRPadEnum {
    QR_PAD0(0xEC),QR_PAD1(0x11);

    public int getPadValue() {
        return padValue;
    }

    public void setPadValue(int padValue) {
        this.padValue = padValue;
    }

    private int padValue;

    QRPadEnum(int padValue){
        this.padValue=padValue;
    }


}
