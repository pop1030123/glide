package com.bumptech.glide.resize;

import com.bumptech.glide.loader.bitmap.resource.ResourceFetcher;
import com.bumptech.glide.resize.cache.DiskCache;

import java.io.OutputStream;

/**
 *
 * @param <T> The type of the data the resource will be decoded from.
 * @param <Z> The type of the resource that will be decoded.
 */
public class SourceResourceRunner<T, Z> implements Runnable, DiskCache.Writer, Prioritized {
    private final String id;
    private final int width;
    private final int height;
    private final ResourceFetcher<T> fetcher;
    private final ResourceDecoder<T, Z> decoder;
    private final ResourceEncoder<Z> encoder;
    private DiskCache diskCache;
    private Metadata metadata;
    private ResourceCallback<Z> cb;
    private Resource<Z> result;
    private volatile boolean isCancelled;

    public SourceResourceRunner(String id, int width, int height, ResourceFetcher<T> resourceFetcher, ResourceDecoder<T, Z> decoder,
            ResourceEncoder<Z> encoder, DiskCache diskCache, Metadata metadata, ResourceCallback<Z> cb) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.fetcher = resourceFetcher;
        this.decoder = decoder;
        this.encoder = encoder;
        this.diskCache = diskCache;
        this.metadata = metadata;
        this.cb = cb;
    }

    public void cancel() {
        isCancelled = true;
        fetcher.cancel();
    }

    @Override
    public void run() {
        if (isCancelled) {
            return;
        }

        try {
            result = null;
            T toDecode = fetcher.loadResource(metadata);
            if (toDecode != null) {
                result = decoder.decode(toDecode, width, height);
            }
            if (result != null) {
                diskCache.put(id, this);
                cb.onResourceReady(result);
            } else {
                cb.onException(null);
            }

        } catch (Exception e) {
            cb.onException(e);
        }
    }

    @Override
    public void write(OutputStream os) {
        encoder.encode(result, os);
    }

    @Override
    public int getPriority() {
        return metadata.getPriority().ordinal();
    }
}