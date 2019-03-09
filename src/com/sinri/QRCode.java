package com.sinri;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;

public class QRCode {
    private int typeNumber;
    private Matrix<Boolean> modules;
    private int moduleCount;
    private QRErrorCorrectLevelEnum errorCorrectLevel;
    private ArrayList<QRData> qrDataList;

    public QRCode()
    {
        this.typeNumber = 1;
        this.errorCorrectLevel = QRErrorCorrectLevelEnum.QR_ERROR_CORRECT_LEVEL_H;
        this.qrDataList = new ArrayList<>();
    }

    static ArrayList<Boolean> createNullArray(int length)
    {
        ArrayList<Boolean> nullArray = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            //$nullArray[] = null;
            nullArray.set(i,null);
        }
        return nullArray;
    }

    private static ArrayList<Integer> createData(int typeNumber, QRErrorCorrectLevelEnum errorCorrectLevel, ArrayList<QRData> dataArray)
    {

        ArrayList<QRRSBlock> rsBlocks = QRRSBlock.getRSBlocks(typeNumber, errorCorrectLevel);

        QRBitBuffer buffer = new QRBitBuffer();

        for (QRData data : dataArray) {
            buffer.put(data.getMode().getModValue(), 4);
            buffer.put(data.getLength(), data.getLengthInBits(typeNumber));
            data.write(buffer);
        }

        int totalDataCount = 0;
        for (QRRSBlock rsBlock : rsBlocks) {
            totalDataCount += rsBlock.getDataCount();
        }

        if (buffer.getLengthInBits() > totalDataCount * 8) {
            throw new RuntimeException("code length overflow. (" +buffer.getLengthInBits() + ">" + totalDataCount * 8 + ")");
        }

        // end code.
        if (buffer.getLengthInBits() + 4 <= totalDataCount * 8) {
            buffer.put(0, 4);
        }

        // padding
        while (buffer.getLengthInBits() % 8 != 0) {
            buffer.putBit(false);
        }

        // padding
        while (true) {

            if (buffer.getLengthInBits() >= totalDataCount * 8) {
                break;
            }
            buffer.put(QRPadEnum.QR_PAD0.getPadValue(), 8);

            if (buffer.getLengthInBits() >= totalDataCount * 8) {
                break;
            }
            buffer.put(QRPadEnum.QR_PAD1.getPadValue(), 8);
        }

        return createBytes(buffer, rsBlocks);
    }

    private static ArrayList<Integer> createBytes(QRBitBuffer buffer, ArrayList<QRRSBlock> rsBlocks)
    {

        int offset = 0;

        int maxDcCount = 0;
        int maxEcCount = 0;

        //ArrayList<Boolean> dcdata = createNullArray(rsBlocks.size());
        //ArrayList<Boolean> ecdata = createNullArray(rsBlocks.size());

        Matrix<Integer> dcdata = new Matrix<>();
        Matrix<Integer> ecdata=new Matrix<>();

        int rsBlockCount = rsBlocks.size();
        for (int r = 0; r < rsBlockCount; r++) {

            int dcCount = rsBlocks.get(r).getDataCount();
            int ecCount = rsBlocks.get(r).getTotalCount() - dcCount;

            maxDcCount = Math.max(maxDcCount, dcCount);
            maxEcCount = Math.max(maxEcCount, ecCount);

            //dcdata.set(r , createNullArray(dcCount));
            dcdata.setRow(r, dcdata.createNullRow(dcCount));
            //dcDataCount = count($dcdata[$r]);

            for (int i = 0; i < dcCount; i++) {
                ArrayList<Integer> bdata = buffer.getBuffer();
                //$dcdata[$r][$i] = 0xff & $bdata[$i + $offset];
                dcdata.setCell(r,i,0xff & bdata.get(i+offset));
            }
            offset += dcCount;

            QRPolynomial rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount);
            QRPolynomial rawPoly = new QRPolynomial(dcdata.getRow(r), rsPoly.getLength() - 1);

            QRPolynomial modPoly = rawPoly.mod(rsPoly);
            //$ecdata[$r] = QRCode::createNullArray(rsPoly.getLength() - 1);
            ecdata.setRow(r,ecdata.createNullRow(rsPoly.getLength()-1));

            //$ecDataCount = count($ecdata[$r]);
            int ecDataCount=ecdata.getRow(r).size();
            for (int i = 0; i < ecDataCount; i++) {
                int modIndex = i + modPoly.getLength() - ecdata.getRow(r).size();//count($ecdata[$r]);
                //$ecdata[$r][$i] = ($modIndex >= 0) ? $modPoly.get($modIndex) : 0;
                ecdata.setCell(r,i,modIndex>=0?modPoly.get(modIndex):0);
            }
        }

//        int totalCodeCount = 0;
//        for (QRRSBlock rsBlock : rsBlocks) {
//            totalCodeCount += rsBlock.getTotalCount();
//        }

        ArrayList<Integer> data = new ArrayList<>();

        fillDataForDEC(data,maxDcCount,dcdata,rsBlockCount);
        fillDataForDEC(data,maxEcCount,ecdata,rsBlockCount);

        return data;
    }

    private static void fillDataForDEC(ArrayList<Integer> data,int maxCount,Matrix<Integer> decdata, int rsBlockCount){
        for (int i = 0; i < maxCount; i++) {
            for (int r = 0; r < rsBlockCount; r++) {
                if (i < decdata.getRow(r).size()) {
                    data.add(decdata.getCell(r,i));
                }
            }
        }
    }

    public static QRCode getMinimumQRCode(String data, QRErrorCorrectLevelEnum errorCorrectLevel)
    {

        QRModeEnum mode = QRUtil.getMode(data);

        System.out.println("Mode: " + mode);

        QRCode qr = new QRCode();
        qr.setErrorCorrectLevel(errorCorrectLevel);
        qr.addData(data, mode);

        QRData qrData = qr.getData(0);
        int length = qrData.getLength();

        for (int typeNumber = 1; typeNumber <= 40; typeNumber++) {
            if (length <= QRUtil.getMaxLength(typeNumber, mode, errorCorrectLevel)) {
                qr.setTypeNumber(typeNumber);
                break;
            }
        }

        qr.make();

        return qr;
    }

    public int getTypeNumber() {
        return typeNumber;
    }

    public void setTypeNumber(int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public QRErrorCorrectLevelEnum getErrorCorrectLevel() {
        return errorCorrectLevel;
    }

    public void setErrorCorrectLevel(QRErrorCorrectLevelEnum errorCorrectLevel) {
        this.errorCorrectLevel = errorCorrectLevel;
    }

    private void addData(String data){
        addData(data,QRModeEnum.QR_MODE_AUTO_DETECT);
    }

    private void addData(String data, QRModeEnum mode)
    {

        if (mode == QRModeEnum.QR_MODE_AUTO_DETECT) {
            mode = QRUtil.getMode(data);
        }

        switch (mode) {
            case QR_MODE_NUMBER :
                this.addDataImpl(new QRNumber(data));
                break;
            case QR_MODE_ALPHA_NUM :
                this.addDataImpl(new QRAlphaNum(data));
                break;
            case QR_MODE_8BIT_BYTE :
                this.addDataImpl(new QR8BitByte(data));
                break;
            case QR_MODE_KANJI :
                this.addDataImpl(new QRKanji(data));
                break;
            default :
                throw new RuntimeException("mode:"+mode);
        }
    }

    private void clearData()
    {
        qrDataList = new ArrayList<>();
    }

    private void addDataImpl(QRData qrData)
    {
        qrDataList.add(qrData);
    }

    private int  getDataCount()
    {
        return qrDataList.size();
    }

    private QRData getData(int index)
    {
        return qrDataList.get(index);
    }

    public boolean isDark(int row, int col)
    {
        return modules.getCell(row,col);//[row][col];
    }

    public int getModuleCount()
    {
        return moduleCount;
    }

    // used for converting fg/bg colors (e.g. #0000ff = 0x0000FF)
    // added 2015.07.27 ~ DoktorJ
    private RGB hex2rgb(int hex)
    {
        return new RGB(hex >> 16,(hex >> 8)%256,hex%256);
    }

    private void make()
    {
        makeImpl(false, getBestMaskPattern());
    }

    private QRMaskEnum getBestMaskPattern()
    {

        int minLostPoint = 0;
        int pattern = 0;

        for (int i = 0; i < 8; i++) {

            makeImpl(true, QRMaskEnum.getMaskEnumByValue(i));

            int lostPoint = QRUtil.getLostPoint(this);

            if (i == 0 || minLostPoint > lostPoint) {
                minLostPoint = lostPoint;
                pattern = i;
            }
        }

        return QRMaskEnum.getMaskEnumByValue(pattern);
    }

    private void makeImpl(boolean test, QRMaskEnum maskPattern)
    {

        moduleCount = typeNumber * 4 + 17;

        modules=new Matrix<>();

        setupPositionProbePattern(0, 0);
        setupPositionProbePattern(moduleCount - 7, 0);
        setupPositionProbePattern(0, moduleCount - 7);

        setupPositionAdjustPattern();
        setupTimingPattern();

        setupTypeInfo(test, maskPattern);

        if (typeNumber >= 7) {
            setupTypeNumber(test);
        }

        ArrayList<QRData> dataArray = qrDataList;

        ArrayList<Integer> data = QRCode.createData(typeNumber, errorCorrectLevel, dataArray);

        mapData(data, maskPattern);
    }

    private void mapData(ArrayList<Integer> data, QRMaskEnum maskPattern)
    {

        int inc = -1;
        int row = moduleCount - 1;
        int bitIndex = 7;
        int byteIndex = 0;

        for (int col = moduleCount - 1; col > 0; col -= 2) {

            if (col == 6) col--;

            while (true) {

                for (int c = 0; c < 2; c++) {

                    if (modules.getCell(row,col-c) == null) {

                        boolean dark = false;

                        if (byteIndex < data.size()) {
                            dark=(((data.get(byteIndex)>>bitIndex)&1)==1);
                        }

                        if (QRUtil.getMask(maskPattern, row, col - c)) {
                            dark = !dark;
                        }

                        modules.setCell(row,col-c,dark);
                        bitIndex--;

                        if (bitIndex == -1) {
                            byteIndex++;
                            bitIndex = 7;
                        }
                    }
                }

                row += inc;

                if (row < 0 || moduleCount <= row) {
                    row -= inc;
                    inc = -inc;
                    break;
                }
            }
        }
    }

    private void setupPositionAdjustPattern()
    {
        int[] pos = QRUtil.getPatternPosition(typeNumber);

        for (int po : pos) {
            for (int po1 : pos) {

                if (modules.getCell(po, po1) != null) continue;

                for (int r = -2; r <= 2; r++) {
                    for (int c = -2; c <= 2; c++) {
                        modules.setCell(po + r, po1 + c, r == -2 || r == 2 || c == -2 || c == 2 || (r == 0 && c == 0));
                    }
                }
            }
        }
    }

    private void setupPositionProbePattern(int row, int col)
    {

        for (int r = -1; r <= 7; r++) {

            for (int c = -1; c <= 7; c++) {

                if (row + r <= -1 || moduleCount <= row + r
                        || col + c <= -1 || moduleCount <= col + c) {
                    continue;
                }

                modules.setCell(
                        row+r,
                        col+c,
                        (0 <= r && r <= 6 && (c == 0 || c == 6))
                                || (0 <= c && c <= 6 && (r == 0 || r == 6))
                                || (2 <= r && r <= 4 && 2 <= c && c <= 4)
                );
            }
        }
    }

    private void setupTimingPattern()
    {
        for (int i = 8; i < moduleCount - 8; i++) {
            if (modules.getCell(i,6) != null || modules.getCell(6,i) != null) {
                continue;
            }

            modules.setCell(i,6,i%2==0);
            modules.setCell(6,i,i%2==0);
        }
    }

    private void setupTypeNumber(boolean test)
    {
        int bits = QRUtil.getBCHTypeNumber(typeNumber);

        for (int i = 0; i < 18; i++) {
            boolean mod = (!test && ((bits >> i) & 1) == 1);
            modules.setCell((int)Math.floor(i/3.0),i%3+moduleCount-8-3,mod);
            modules.setCell(i%3+moduleCount-8-3,(int)Math.floor(i/3.0),mod);
        }
    }

    private void setupTypeInfo(boolean test, QRMaskEnum maskPattern)
    {

        int data = (errorCorrectLevel.getLevelValue() << 3) | maskPattern.getMaskValue();
        int bits = QRUtil.getBCHTypeInfo(data);

        for (int i = 0; i < 15; i++) {

            boolean mod = (!test && ((bits >> i) & 1) == 1);

            if (i < 6) {
                modules.setCell(i,8, mod);
            } else if (i < 8) {
                modules.setCell(i+1,8,mod);
            } else {
                modules.setCell(moduleCount-15+i,8,mod);
            }

            if (i < 8) {
                modules.setCell(8,moduleCount-i-1,mod);
            } else if (i < 9) {
                modules.setCell(8,15-i-1+1,mod);
            } else {
                modules.setCell(8,15-i-1,mod);
            }
        }

        modules.setCell(moduleCount-8,8,!test);
    }

    class RGB{
        final int r;
        final int g;
        final int b;

        RGB(int r, int g, int b){
            this.r=r;
            this.g=g;
            this.b=b;
        }
    }


    public RenderedImage createImage(int cellSize, Color backgroundColor, Color foregroundColor) {
        BufferedImage bufferedImage = new BufferedImage(cellSize * (getModuleCount() + 2), cellSize * (getModuleCount() + 2), BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bufferedImage.createGraphics();

        // write the cells

        g2d.setColor(backgroundColor);//Color.white
        g2d.fillRect(0, 0, cellSize * (getModuleCount() + 2), cellSize * (getModuleCount() + 2));
        //g2d.setColor(foregroundColor);//Color.black
        //g2d.fillOval(0, 0, width, height);

        for (int i = 0; i < getModuleCount(); i++) {
            for (int j = 0; j < getModuleCount(); j++) {
                if (isDark(i, j)) {
                    g2d.setColor(foregroundColor);
                    g2d.fillRect(cellSize * (1 + i), cellSize * (1 + j), cellSize, cellSize);
                }
            }
        }

        g2d.dispose();
        return bufferedImage;

//        File file = new File("newimage.png");
//        ImageIO.write(rendImage, "png", file);
//
//        file = new File("newimage.jpg");
//        ImageIO.write(rendImage, "jpg", file);
    }

//    /**
//     * added $fg (foreground), $bg (background), and $bgtrans (use transparent bg) parameters
//     * also added some simple error checking on parameters
//     * updated 2015.07.27 ~ DoktorJ
//     *
//     * @param int $size
//     * @param int $margin
//     * @param int $foregroundColor
//     * @param int $backgroundColor
//     * @param bool $backgroundBeTransparent
//     * @return resource
//     */
//    function createImage($size = 2, $margin = 2, $foregroundColor = 0x000000, $backgroundColor = 0xFFFFFF, $backgroundBeTransparent = false)
//    {
//
//        // size/margin EC
//        if (!is_numeric($size)) $size = 2;
//        if (!is_numeric($margin)) $margin = 2;
//        if ($size < 1) $size = 1;
//        if ($margin < 0) $margin = 0;
//
//        $image_size = $this->getModuleCount() * $size + $margin * 2;
//
//        $image = imagecreatetruecolor($image_size, $image_size);
//
//        // fg/bg EC
//        if ($foregroundColor < 0 || $foregroundColor > 0xFFFFFF) $foregroundColor = 0x0;
//        if ($backgroundColor < 0 || $backgroundColor > 0xFFFFFF) $backgroundColor = 0xFFFFFF;
//
//        // convert hexadecimal RGB to arrays for imagecolorallocate
//        $fgrgb = $this->hex2rgb($foregroundColor);
//        $bgrgb = $this->hex2rgb($backgroundColor);
//
//        // replace $black and $white with $fgc and $bgc
//        $fgc = imagecolorallocate($image, $fgrgb['r'], $fgrgb['g'], $fgrgb['b']);
//        $bgc = imagecolorallocate($image, $bgrgb['r'], $bgrgb['g'], $bgrgb['b']);
//        if ($backgroundBeTransparent) imagecolortransparent($image, $bgc);
//
//        // update $white to $bgc
//        imagefilledrectangle($image, 0, 0, $image_size, $image_size, $bgc);
//
//        for ($r = 0; $r < $this->getModuleCount(); $r++) {
//            for ($c = 0; $c < $this->getModuleCount(); $c++) {
//                if ($this->isDark($r, $c)) {
//
//                    // update $black to $fgc
//                    imagefilledrectangle($image,
//                            $margin + $c * $size,
//                            $margin + $r * $size,
//                            $margin + ($c + 1) * $size - 1,
//                            $margin + ($r + 1) * $size - 1,
//                            $fgc);
//                }
//            }
//        }
//
//        return $image;
//    }

//    /**
//     * Output a <table> code block
//     * @param string $size
//     */
//    function printHTML($size = "2px")
//    {
//
//        $style = "border-style:none;border-collapse:collapse;margin:0px;padding:0px;";
//
//        print("<table style='$style'>");
//
//        for ($r = 0; $r < $this->getModuleCount(); $r++) {
//
//            print("<tr style='$style'>");
//
//            for ($c = 0; $c < $this->getModuleCount(); $c++) {
//                $color = $this->isDark($r, $c) ? "#000000" : "#ffffff";
//                print("<td style='$style;width:$size;height:$size;background-color:$color'></td>");
//            }
//
//            print("</tr>");
//        }
//
//        print("</table>");
//    }

//    /**
//     * @return boolean[][]
//     */
//    public boolean[][] getQRMatrix()
//    {
//        boolean[][] matrix={};
//
//        for(int r=0;r<getModuleCount();r++){
//            for(int c=0;c<getModuleCount();c++){
//                matrix[r][c]=isDark(r,c);
//            }
//        }
//        return matrix;
//    }
}
