package com.adobe.cq.email.core.components.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.SlingRequestProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.day.cq.contentsync.handler.util.RequestResponseFactory;

import static com.adobe.cq.email.core.components.TestFileUtils.EXTERNAL_CSS_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_AND_EXTERNAL_CSS_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.INTERNAL_CSS_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.OUTPUT_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.STYLE_FILE_PATH;
import static com.adobe.cq.email.core.components.TestFileUtils.compare;
import static com.adobe.cq.email.core.components.TestFileUtils.getFileContent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StyleExtractorTest {

    @Mock
    RequestResponseFactory requestResponseFactory;
    @Mock
    SlingRequestProcessor requestProcessor;
    @Mock
    ResourceResolver resourceResolver;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    @Test
    void extractFromExternalCssFileOnly() throws URISyntaxException, IOException, ServletException {
        String cssStyle = getFileContent(STYLE_FILE_PATH);
        when(requestResponseFactory.createRequest(eq("GET"), anyString(), anyMap())).thenReturn(request);
        AtomicReference<OutputStream> osReference = new AtomicReference<>();
        doAnswer(i -> {
            osReference.set(i.getArgument(0));
            return response;
        }).when(requestResponseFactory).createResponse(any());
        doAnswer(i -> {
            osReference.get().write(cssStyle.getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(requestProcessor).processRequest(eq(request), eq(response), any());
        Document document = Jsoup.parse(getFileContent(EXTERNAL_CSS_FILE_PATH));
        List<String> styles = StyleExtractor.extract(document,
                requestResponseFactory, requestProcessor, resourceResolver
        );
        assertEquals(1, styles.size());
        compare(cssStyle, styles.get(0));
        compare(getFileContent(OUTPUT_FILE_PATH), document.outerHtml());
    }

    @Test
    void extractFromInternalStyleTagOnly() throws URISyntaxException, IOException {
        Document document = Jsoup.parse(getFileContent(INTERNAL_CSS_FILE_PATH));
        List<String> styles = StyleExtractor.extract(document,
                requestResponseFactory, requestProcessor, resourceResolver
        );
        assertEquals(1, styles.size());
        compare(getFileContent(STYLE_FILE_PATH), styles.get(0));
        compare(getFileContent(OUTPUT_FILE_PATH), document.outerHtml());
    }

    @Test
    void extractBothInternalAndExternal() throws URISyntaxException, IOException, ServletException {
        String cssStyle = getFileContent(STYLE_FILE_PATH);
        when(requestResponseFactory.createRequest(eq("GET"), anyString(), anyMap())).thenReturn(request);
        AtomicReference<OutputStream> osReference = new AtomicReference<>();
        doAnswer(i -> {
            osReference.set(i.getArgument(0));
            return response;
        }).when(requestResponseFactory).createResponse(any());
        doAnswer(i -> {
            osReference.get().write(cssStyle.getBytes(StandardCharsets.UTF_8));
            return null;
        }).when(requestProcessor).processRequest(eq(request), eq(response), any());
        Document document = Jsoup.parse(getFileContent(INTERNAL_AND_EXTERNAL_CSS_FILE_PATH));
        List<String> styles = StyleExtractor.extract(document,
                requestResponseFactory, requestProcessor, resourceResolver
        );
        assertEquals(2, styles.size());
        compare(cssStyle, styles.get(0));
        compare(cssStyle, styles.get(1));
        compare(getFileContent(OUTPUT_FILE_PATH), document.outerHtml());
    }

}