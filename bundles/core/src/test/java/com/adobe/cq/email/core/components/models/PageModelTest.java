/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
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