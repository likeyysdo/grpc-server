package com.lncn.rsql.remotejdbc.encode;

/**
 * @Classname AdaptFastByteArrayOutputStream
 * @Description TODO
 * @Date 2022/7/28 14:09
 * @Created by byco
 */
public class AdaptFastByteArrayOutputStream extends FastByteArrayOutputStream{
    /**
     * Create a new <code>FastByteArrayOutputStream</code>
     * with the default initial capacity of 256 bytes.
     */
    public AdaptFastByteArrayOutputStream() {
    }

    /**
     * Create a new <code>FastByteArrayOutputStream</code>
     * with the specified initial capacity.
     *
     * @param initialBlockSize the initial buffer size in bytes
     */
    public AdaptFastByteArrayOutputStream(int initialBlockSize) {
        super(initialBlockSize);
    }

    public AdaptFastByteArrayOutputStream(int initialBlockSize, boolean predictSize) {
        super(initialBlockSize, predictSize);
    }

    public AdaptFastByteArrayOutputStream(int initialBlockSize, boolean predictSize, int nextSize) {
        super(initialBlockSize, predictSize, nextSize);
    }
}
