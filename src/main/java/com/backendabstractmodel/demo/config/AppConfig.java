package com.backendabstractmodel.demo.config;

import com.backendabstractmodel.demo.config.exception.ResourceExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Configuration
public class AppConfig extends ResourceExceptionHandler {
}
