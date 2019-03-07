package com.sinri;

public enum QRErrorCorrectLevelEnum {
     QR_ERROR_CORRECT_LEVEL_L (1),// 7%.
     QR_ERROR_CORRECT_LEVEL_M (0),// 15%.
     QR_ERROR_CORRECT_LEVEL_Q (3),// 25%.
     QR_ERROR_CORRECT_LEVEL_H (2);// 30%.

    public int getLevelValue() {
        return levelValue;
    }

    public void setLevelValue(int levelValue) {
        this.levelValue = levelValue;
    }

    private int levelValue;

    QRErrorCorrectLevelEnum(int levelValue){
        this.levelValue=levelValue;
    }


}
