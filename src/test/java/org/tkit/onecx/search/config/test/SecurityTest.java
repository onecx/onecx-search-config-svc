package org.tkit.onecx.search.config.test;

import java.util.List;

import org.tkit.quarkus.security.test.AbstractSecurityTest;
import org.tkit.quarkus.security.test.SecurityTestConfig;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SecurityTest extends AbstractSecurityTest {
    @Override
    public SecurityTestConfig getConfig() {
        SecurityTestConfig config = new SecurityTestConfig();
        config.addConfig("read", "/internal/searchConfig/search", 400, List.of("ocx-sc:read"), "post");
        config.addConfig("write", "/internal/searchConfig", 400, List.of("ocx-sc:write"), "post");
        config.addConfig("delete", "/internal/searchConfig/id", 204, List.of("ocx-sc:delete"), "delete");
        return config;
    }
}
