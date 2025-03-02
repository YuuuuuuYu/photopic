package com.swyp8team2.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiscordMessageSender {

    private final DiscordClient discordClient;
    private final Clock clock;
    private final Environment environment;

    public void sendDiscordAlarm(Exception e, WebRequest request) {
        discordClient.sendAlarm(createMessage(e, request));
    }

    private DiscordMessage createMessage(Exception e, WebRequest request) {
        List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        return DiscordMessage.builder()
                .content("# 🚨 에러 발생")
                .embeds(
                        List.of(
                                DiscordMessage.Embed.builder()
                                        .title("ℹ️ 에러 정보")
                                        .description(
                                                """
                                                        ### 📝 환경 정보
                                                        %s
                                                        ### 🕖 발생 시간
                                                        %s
                                                        ### 🔗 요청 URL
                                                        %s
                                                        ### 📄 Stack Trace
                                                        ```
                                                        %s
                                                        ```
                                                        """.formatted(
                                                        String.join("", profiles),
                                                        Instant.now(clock),
                                                        createRequestFullPath(request),
                                                        getStackTrace(e).substring(0, 1000)
                                                ))
                                        .build()
                        )
                )
                .build();
    }

    private String createRequestFullPath(WebRequest webRequest) {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        String fullPath = request.getMethod() + " " + request.getRequestURL();

        String queryString = request.getQueryString();
        if (queryString != null) {
            fullPath += "?" + queryString;
        }

        return fullPath;
    }

    private String getStackTrace(Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }
}
