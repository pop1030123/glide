package com.bumptech.glide.resize;

import com.bumptech.glide.loader.bitmap.resource.ResourceFetcher;
import com.bumptech.glide.resize.cache.ResourceCache;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EngineTest {
    private static final String ID = "testId";
    private EngineTestHarness harness;

    @Before
    public void setUp() {
        harness = new EngineTestHarness();
    }

    @Test
    public void testNewRunnerIsCreatedAndPostedWithNoExistingLoad() {
        harness.doLoad();

        verify(harness.runner).queue();
    }

    @Test
    public void testCallbackIsAddedToNewRunners() {
        harness.doLoad();

        verify(harness.job).addCallback(eq(harness.cb));
    }

    @Test
    public void testLoadStatusIsReturnedForNewLoad() {
        assertNotNull(harness.doLoad());
    }

    @Test
    public void testEngineJobReceivesCancelFromLoadStatus() {
        Engine.LoadStatus loadStatus = harness.doLoad();
        loadStatus.cancel();

        verify(harness.job).cancel();
    }

    @Test
    public void testNewRunnerIsNotCreatedAndPostedWithExistingLoad() {
        harness.doLoad();
        harness.doLoad();

        verify(harness.runner, times(1)).queue();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCallbackIsAddedToExistingRunnerWithExistingLoad() {
        harness.doLoad();
        harness.doLoad();

        verify(harness.runner.getJob(), times(2)).addCallback(any(ResourceCallback.class));
    }

    @Test
    public void testLoadStatusIsReturnedForExistingJob() {
        harness.doLoad();
        Engine.LoadStatus loadStatus = harness.doLoad();

        assertNotNull(loadStatus);
    }

    @Test
    public void testResourceIsReturnedFromCacheIfPresent() {
        when(harness.cache.get(eq(ID))).thenReturn(harness.result);

        harness.doLoad();

        verify(harness.cb).onResourceReady(eq(harness.result));
    }

    @Test
    public void testNewLoadIsNotStartedIfResourceIsCached() {
        when(harness.cache.get(eq(ID))).thenReturn(mock(Resource.class));

        harness.doLoad();

        verify(harness.runner, never()).queue();
    }

    @Test
    public void testNullLoadStatusIsReturnedForCachedResource() {
        when(harness.cache.get(eq(ID))).thenReturn(mock(Resource.class));

        Engine.LoadStatus loadStatus = harness.doLoad();
        assertNull(loadStatus);
    }

    @SuppressWarnings("unchecked")
    private static class EngineTestHarness {
        ResourceDecoder<InputStream, Object> cacheDecoder = mock(ResourceDecoder.class);
        ResourceFetcher<Object> fetcher = mock(ResourceFetcher.class);
        ResourceDecoder<Object, Object> decoder = mock(ResourceDecoder.class);
        ResourceEncoder<Object> encoder = mock(ResourceEncoder.class);
        Metadata metadata = mock(Metadata.class);
        ResourceCallback<Object> cb = mock(ResourceCallback.class);
        Resource<Object> result = mock(Resource.class);
        int width = 100;
        int height = 100;

        ResourceCache cache = mock(ResourceCache.class);
        ResourceRunner<Object> runner = mock(ResourceRunner.class);
        EngineJob<Object> job;
        Engine engine;

        public EngineTestHarness() {
            ResourceRunnerFactory factory = mock(ResourceRunnerFactory.class);
            when(factory.build(eq(ID), eq(width), eq(height), eq(cacheDecoder), eq(fetcher), eq(decoder), eq(encoder),
                    eq(metadata))).thenReturn(runner);

            job = mock(EngineJob.class);
            when(runner.getJob()).thenReturn(job);

            engine = new Engine(factory, cache);
        }

        public Engine.LoadStatus doLoad() {
            return engine.load(ID, width, height, cacheDecoder, fetcher, decoder, encoder, metadata, cb);
        }
    }
}