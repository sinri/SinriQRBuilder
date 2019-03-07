package com.sinri;

import java.util.ArrayList;

class QRBitBuffer {
    private final ArrayList<Integer> buffer;
    private int length;

    QRBitBuffer()
    {
        this.buffer = new ArrayList<>();
        this.length = 0;
    }

    ArrayList<Integer> getBuffer()
    {
        return this.buffer;
    }

    int getLengthInBits()
    {
        return this.length;
    }

    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < getLengthInBits(); i++) {
            buffer .append(get(i) ? '1' : '0');
        }
        return buffer.toString();
    }

    private boolean get(int index)
    {
        int bufIndex = (int) Math.floor(index * 1.0 / 8);
        return ((buffer.get(bufIndex) >> (7 - index % 8)) & 1) == 1;
    }

    void put(int num, int length)
    {
        for (int i = 0; i < length; i++) {
            putBit(((num >> (length - i - 1)) & 1) == 1);
        }
    }

    void putBit(boolean bit)
    {

        int bufIndex = (int) Math.floor(length * 1.0 / 8);
        if (buffer.size() <= bufIndex) {
            buffer.add( 0);
        }

        if (bit) {
            buffer.set(bufIndex,buffer.get(bufIndex)|(0x80>>(length%8)));
        }

        length++;
    }
}
