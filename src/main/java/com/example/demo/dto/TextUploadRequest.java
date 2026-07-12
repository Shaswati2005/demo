package com.example.demo.dto;

import lombok.Data;

@Data
public class TextUploadRequest {

    private String title;

    private String subject;

    private String text;
}