package com.bumptech.glide.load.resource.file;

import com.bumptech.glide.load.engine.Resource;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FileDecoderTest {

    private FileDecoder decoder;

    @Before
    public void setUp() {
        decoder = new FileDecoder();
    }

    @Test
    public void testReturnsEmptyId() {
        assertEquals("", decoder.getId());
    }

    @Test
    public void testReturnsGivenFileAsResource() throws IOException {
        File expected = new File("testFile");
        Resource<File> decoded = decoder.decode(expected, 1, 1);

        assertEquals(expected, decoded.get());
    }
}