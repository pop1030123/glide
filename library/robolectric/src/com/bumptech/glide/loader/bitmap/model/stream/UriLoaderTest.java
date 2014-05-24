package com.bumptech.glide.loader.bitmap.model.stream;

import android.content.Context;
import android.net.Uri;
import com.bumptech.glide.loader.GlideUrl;
import com.bumptech.glide.loader.bitmap.model.ModelLoader;
import com.bumptech.glide.loader.bitmap.model.UriLoader;
import com.bumptech.glide.loader.bitmap.resource.ResourceFetcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests for the {@link StreamUriLoader} class.
 */
@RunWith(RobolectricTestRunner.class)
public class UriLoaderTest {
    // Not a magic number, just arbitrary non zero.
    private static final int IMAGE_SIDE = 120;

    private UriLoader loader;
    private ResourceFetcher<InputStream> localUriFetcher;
    private ModelLoader<GlideUrl, InputStream> urlLoader;

    @Before
    public void setUp() throws Exception {
        urlLoader = mock(ModelLoader.class);
        localUriFetcher = mock(ResourceFetcher.class);

        loader = new UriLoader<InputStream>(Robolectric.application, urlLoader) {
            @Override
            protected ResourceFetcher<InputStream> getLocalUriFetcher(Context context, Uri uri) {
                return localUriFetcher;
            }
        };
    }

    @Test
    public void testHandlesFileUris() throws IOException {
        Uri fileUri = Uri.fromFile(new File("f"));
        ResourceFetcher resourceFetcher = loader.getResourceFetcher(fileUri, IMAGE_SIDE, IMAGE_SIDE);
        assertEquals(localUriFetcher, resourceFetcher);
    }

    @Test
    public void testHandlesResourceUris() throws IOException {
        Uri resourceUri = Uri.parse("android.resource://com.bumptech.glide.tests/raw/ic_launcher");
        ResourceFetcher resourceFetcher = loader.getResourceFetcher(resourceUri, IMAGE_SIDE, IMAGE_SIDE);
        assertEquals(localUriFetcher, resourceFetcher);
    }

    @Test
    public void testHandlesContentUris() {
        Uri contentUri = Uri.parse("content://com.bumptech.glide");
        ResourceFetcher resourceFetcher = loader.getResourceFetcher(contentUri, IMAGE_SIDE, IMAGE_SIDE);
        assertEquals(localUriFetcher, resourceFetcher);
    }

    @Test
    public void testHandlesHttpUris() throws MalformedURLException {
        Uri httpUri = Uri.parse("http://www.google.com");
        loader.getResourceFetcher(httpUri, IMAGE_SIDE, IMAGE_SIDE);

        verify(urlLoader).getResourceFetcher(eq(new GlideUrl(httpUri.toString())), eq(IMAGE_SIDE), eq(IMAGE_SIDE));
    }

    @Test
    public void testHandlesHttpsUris() throws MalformedURLException {
        Uri httpsUri = Uri.parse("https://www.google.com");
        loader.getResourceFetcher(httpsUri, IMAGE_SIDE, IMAGE_SIDE);

        verify(urlLoader).getResourceFetcher(eq(new GlideUrl(httpsUri.toString())), eq(IMAGE_SIDE), eq(IMAGE_SIDE));
    }
}