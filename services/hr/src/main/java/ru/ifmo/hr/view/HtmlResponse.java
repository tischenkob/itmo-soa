package ru.ifmo.hr.view;

import lombok.Value;

@Value(staticConstructor = "of")
public class HtmlResponse {
    String templateName;
    Object content;
}