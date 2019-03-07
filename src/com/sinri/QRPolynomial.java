package com.sinri;

import java.util.ArrayList;

class QRPolynomial {
    private final ArrayList<Integer> num;

    QRPolynomial(ArrayList<Integer> num){
        this(num,0);
    }

    QRPolynomial(ArrayList<Integer> num, int shift)
    {
        int offset = 0;

        while (offset < num.size() && num.get(offset) == 0) {
            offset++;
        }

        this.num = QRMath.createNumArray(num.size() - offset + shift);
        for (int i = 0; i < num.size() - offset; i++) {
            this.num.set(i,num.get(i+offset));
        }
    }

    Integer get(int index)
    {
        return num.get(index);
    }

    int getLength()
    {
        return num.size();
    }

    // PHP5
//    function __toString()
//    {
//        return $this->toString();
//    }

    public String toString()
    {

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < getLength(); i++) {
            if (i > 0) {
                buffer .append( ",");
            }
            buffer.append(get(i));
        }

        return buffer.toString();
    }

    String toLogString(){

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i <getLength(); i++) {
            if (i > 0) {
                buffer .append( ",");
            }
            buffer.append( QRMath.glog(get(i)));//I changed it to `$i` from original `i`, refer to issue:  https://github.com/kazuhikoarase/qrcode-generator/issues/60
        }

        return buffer.toString();
    }

    QRPolynomial multiply(QRPolynomial e) {

        ArrayList<Integer> numArray = QRMath.createNumArray(getLength() + e.getLength() - 1);

        for (int i = 0; i < getLength(); i++) {
            Integer vi = QRMath.glog(get(i));

            for (int j = 0; j < e.getLength(); j++) {
                numArray.set(i+j,numArray.get(i+j)^QRMath.gexp(vi+QRMath.glog(e.get(j))));
            }
        }

        return new QRPolynomial(numArray);
    }

    QRPolynomial mod(QRPolynomial e) {

        if (getLength() - e.getLength() < 0) {
            return this;
        }

        Integer ratio = QRMath.glog(get(0)) - QRMath.glog(e.get(0));

        ArrayList<Integer> numArray = QRMath.createNumArray(getLength());
        for (int i = 0; i < getLength(); i++) {
            numArray.set(i, get(i));
        }

        for (int i = 0; i < e.getLength(); i++) {
            numArray.set(i,numArray.get(i)^QRMath.gexp(QRMath.glog(e.get(i))+ratio));
        }

        QRPolynomial newPolynomial = new QRPolynomial(numArray);
        return newPolynomial.mod(e);
    }
}
