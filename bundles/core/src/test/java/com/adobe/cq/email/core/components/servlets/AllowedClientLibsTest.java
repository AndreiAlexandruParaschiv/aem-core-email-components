package com.adobe.cq.email.core.components.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllowedClientLibsTest {
    @Mock
    SlingHttpServletRequest request;
    @Mock
    SlingHttpServletResponse response;
    @Mock
    ResourceResolver resolver;
    @Mock
    ContentPolicyManager policyMgr;
    @Mock
    Resource contentResource;
    @Mock
    private ContentPolicy policy;
    @Mock
    private Resource policyRes;
    @Mock
    private Resource clientLibRes;

    private AllowedClientLibs sut;

    @Test
    void success() throws ServletException, IOException {
        when(request.getResourceResolver()).thenReturn(resolver);
        when(request.getAttribute(eq(Value.CONTENTPATH_ATTRIBUTE))).thenReturn("contentResource");
        when(resolver.getResource(eq("contentResource"))).thenReturn(contentResource);
        when(resolver.adaptTo(eq(ContentPolicyManager.class))).thenReturn(policyMgr);
        when(policyMgr.getPolicy(eq(contentResource))).thenReturn(policy);
        when(policy.adaptTo(eq(Resource.class))).thenReturn(policyRes);
        when(resolver.getResource(eq(policyRes), eq(AllowedClientLibs.PN_ALLOWED_CLIENT_LIBS_PATH))).thenReturn(clientLibRes);
        Iterable<Resource> children =
                Arrays.asList(mockClientLibChildRes("first", "first/clientlib"), mockClientLibChildRes("second", "second/clientlib"),
                        mockClientLibChildRes("third", "third/clientlib"), mockClientLibChildRes("fourth", "fourth/clientlib"));
        when(clientLibRes.getChildren()).thenReturn(children);
        sut = new AllowedClientLibs();
        sut.doGet(request, response);
        ArgumentCaptor<SimpleDataSource> argumentCaptor = ArgumentCaptor.forClass(SimpleDataSource.class);
        verify(request, times(1)).setAttribute(eq(DataSource.class.getName()), argumentCaptor.capture());
        SimpleDataSource dataSource = argumentCaptor.getValue();
        assertNotNull(dataSource);
        List<Pair<String, String>> clientLibs = new ArrayList<>();
        Iterator<Resource> iterator = dataSource.iterator();
        iterator.forEachRemaining(e -> {
            assertNotNull(e);
            ValueMap valueMap = e.getValueMap();
            clientLibs.add(Pair.of(valueMap.get("text", String.class), valueMap.get("value", String.class)));
        });
        assertEquals(Arrays.asList(Pair.of("first", "first/clientlib"), Pair.of("second", "second/clientlib"),
                Pair.of("third", "third/clientlib"), Pair.of("fourth", "fourth/clientlib")), clientLibs);
    }

    public Resource mockClientLibChildRes(String name, String clientLib) {
        Resource resource = mock(Resource.class);
        ValueMap valueMap = mock(ValueMap.class);
        when(resource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get(eq(AllowedClientLibs.PN_ALLOWED_CLIENT_LIBS_NAME), eq(String.class))).thenReturn(name);
        when(valueMap.get(eq(AllowedClientLibs.PN_ALLOWED_CLIENT_LIBS), eq(String.class))).thenReturn(clientLib);
        return resource;
    }
}