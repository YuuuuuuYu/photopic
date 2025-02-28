package com.swyp8team2.support;

import com.swyp8team2.common.presentation.CustomHeader;
import com.swyp8team2.support.config.RestDocsConfiguration;
import com.swyp8team2.support.config.TestSecurityConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;

@AutoConfigureRestDocs
@Import({RestDocsConfiguration.class, TestSecurityConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class RestDocsTest extends WebUnitTest {

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    protected static Attributes.Attribute constraints(String value) {
        return new Attributes.Attribute("constraints", value);
    }

    protected static HeaderDescriptor authorizationHeader() {
        return headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer token");
    }

    protected static HeaderDescriptor guestHeader() {
        return headerWithName(CustomHeader.GUEST_TOKEN).description("게스트 토큰");
    }

    protected static ParameterDescriptor[] cursorQueryParams() {
        return new ParameterDescriptor[]{
                parameterWithName("cursor").optional().description("페이지 조회 커서 값"),
                parameterWithName("size").optional().attributes(defaultValue("10")).description("페이지 크기 (기본 값 10)")
        };
    }

    protected static Attributes.Attribute defaultValue(String value) {
        return new Attributes.Attribute("defaultValue", value);
    }
}
