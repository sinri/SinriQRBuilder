package com.sinri;

abstract class QRData {
    private final QRModeEnum mode;

    private final String data;

    QRData(QRModeEnum mode, String data)
    {
        this.mode = mode;
        this.data = data;
    }

    QRModeEnum getMode()
    {
        return this.mode;
    }

    String getData()
    {
        return this.data;
    }

    /**
     * @return int
     */
    int getLength()
    {
        return this.data.length();
    }

    abstract void write(QRBitBuffer buffer);

    int getLengthInBits(int type)  {

        if (1 <= type && type < 10) {

            // 1 - 9

            switch (mode) {
                case QR_MODE_NUMBER     :
                    return 10;
                case QR_MODE_ALPHA_NUM     :
                    return 9;
                case QR_MODE_8BIT_BYTE    :
                    return 8;
                case QR_MODE_KANJI      :
                    return 8;
                default :
                    throw new RuntimeException("Unknown Mode");
            }


        } else if (type < 27) {

            // 10 - 26

            switch (mode) {
                case QR_MODE_NUMBER     :
                    return 12;
                case QR_MODE_ALPHA_NUM     :
                    return 11;
                case QR_MODE_8BIT_BYTE    :
                    return 16;
                case QR_MODE_KANJI      :
                    return 10;
                default :
                    throw new RuntimeException("Unknown Mode");
            }

        } else if (type < 41) {

            // 27 - 40

            switch (mode) {
                case QR_MODE_NUMBER     :
                    return 14;
                case QR_MODE_ALPHA_NUM    :
                    return 13;
                case QR_MODE_8BIT_BYTE    :
                    return 16;
                case QR_MODE_KANJI      :
                    return 12;
                default :
                    throw new RuntimeException("Unknown Mode");
            }

        } else {
            //trigger_error("mode:$this->mode", E_USER_ERROR);
            throw new RuntimeException("Unknown type");
        }
    }
}
