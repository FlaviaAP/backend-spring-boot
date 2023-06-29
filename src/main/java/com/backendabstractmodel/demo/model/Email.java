package com.backendabstractmodel.demo.model;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Email {

    private String to;
    private String subject;
    private String body;
    private Map<String, Object> args;

}
