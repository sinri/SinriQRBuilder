package com.sinri;

public enum QRMaskEnum {
    QR_MASK_PATTERN000(0),
    QR_MASK_PATTERN001(1),
    QR_MASK_PATTERN010(2),
    QR_MASK_PATTERN011(3),
    QR_MASK_PATTERN100(4),
    QR_MASK_PATTERN101(5),
    QR_MASK_PATTERN110(6),
    QR_MASK_PATTERN111(7);

    public int getMaskValue() {
        return maskValue;
    }

    public void setMaskValue(int maskValue) {
        this.maskValue = maskValue;
    }

    private int maskValue;

    QRMaskEnum(int maskValue) {
        this.maskValue = maskValue;
    }

    static QRMaskEnum getMaskEnumByValue(int value){
        switch (value){
            case 0:return QR_MASK_PATTERN000;
            case 1:return QR_MASK_PATTERN001;
            case 2:return QR_MASK_PATTERN010;
            case 3:return QR_MASK_PATTERN011;
            case 4:return QR_MASK_PATTERN100;
            case 5:return QR_MASK_PATTERN101;
            case 6:return QR_MASK_PATTERN110;
            case 7:return QR_MASK_PATTERN111;
            default:
                throw new RuntimeException("No such mask");
        }
    }
}
