package com.adobe.cq.email.core.components.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PageModelTest {

    private PageModel sut;

    @Test
    void nullAllowedClientLibs() {
        sut = new PageModel();
        assertEquals(0, sut.getAllowedClientLib().length);
    }

    @Test
    void emptyAllowedClientLibs() {
        sut = new PageModel();
        sut.allowedClientLib = new String[0];
        assertEquals(0, sut.getAllowedClientLib().length);
    }

    @Test
    void success() {
        sut = new PageModel();
        String[] allowedClientLib = {"first", "second", "third", "fourth"};
        sut.allowedClientLib = allowedClientLib;
        assertArrayEquals(allowedClientLib, sut.getAllowedClientLib());
    }
}