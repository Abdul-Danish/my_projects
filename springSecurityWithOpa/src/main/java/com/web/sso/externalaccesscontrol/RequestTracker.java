package com.web.sso.externalaccesscontrol;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Getter;
import lombok.Setter;

@Component
@RequestScope
@Getter
@Setter
public class RequestTracker {
    private String method;
    private String uri;
}
