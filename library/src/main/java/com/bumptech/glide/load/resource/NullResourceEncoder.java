package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.OutputStream;

/**
 * A simple {@link com.bumptech.glide.load.ResourceEncoder} that never writes data.
 */
public class NullResourceEncoder<T> implements ResourceEncoder<T> {
    private static final NullResourceEncoder NULL_ENCODER = new NullResourceEncoder();

    /**
     * Returns a NullResourceEncoder for the given type.
     *
     * @param <T> The type of data to be written (or in this case not written).
     */
    @SuppressWarnings("unchecked")
    public static <T> NullResourceEncoder<T> get() {
        return NULL_ENCODER;
    }

    @Override
    public boolean encode(Resource<T> data, OutputStream os) {
        return false;
    }

    @Override
    public String getId() {
        return "";
    }
}