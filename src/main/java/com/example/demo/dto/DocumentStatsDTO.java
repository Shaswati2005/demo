package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentStatsDTO {

    private long totalDocuments;

    private long pdfCount;

    private long docxCount;

    private long textCount;

    private long uploadedToday;

    private long uploadedThisWeek;
}