package com.example.demo.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfGeneratorUtil {

    private PdfGeneratorUtil() {}

    public static byte[] generateQuestionPaperPdf(
            String subject,
            String difficulty,
            String bloomLevel,
            Integer totalMarks,
            String contentText
    ) throws IOException {

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDFont titleFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
            float yPosition = yStart;

            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            // 1. Header Block
            contentStream.beginText();
            contentStream.setFont(titleFont, 18);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("EXAMINATION QUESTION PAPER");
            contentStream.endText();
            yPosition -= 25;

            // Subject Line
            contentStream.beginText();
            contentStream.setFont(boldFont, 12);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Subject: " + (subject != null ? subject : "N/A"));
            contentStream.endText();

            // Total Marks
            String marksStr = "Total Marks: " + (totalMarks != null ? totalMarks : "N/A");
            float marksWidth = boldFont.getStringWidth(marksStr) / 1000f * 12;
            contentStream.beginText();
            contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - marksWidth, yPosition);
            contentStream.showText(marksStr);
            contentStream.endText();
            yPosition -= 15;

            // Difficulty & Bloom Level
            contentStream.beginText();
            contentStream.setFont(regularFont, 10);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Difficulty: " + (difficulty != null ? difficulty : "N/A") + 
                                   "  |  Bloom Level: " + (bloomLevel != null ? bloomLevel : "N/A"));
            contentStream.endText();
            yPosition -= 15;

            // Divider Line
            contentStream.setLineWidth(1.5f);
            contentStream.moveTo(margin, yPosition);
            contentStream.lineTo(page.getMediaBox().getWidth() - margin, yPosition);
            contentStream.stroke();
            yPosition -= 25;

            contentStream.setFont(regularFont, 10);
            float fontSize = 10;
            float leading = 1.5f * fontSize;

            String[] lines = contentText.split("\n");
            for (String line : lines) {
                List<String> wrappedLines = wrapText(line, regularFont, fontSize, tableWidth);
                for (String wrappedLine : wrappedLines) {
                    if (yPosition < margin + 40) {
                        contentStream.close();
                        page = new PDPage(PDRectangle.A4);
                        doc.addPage(page);
                        contentStream = new PDPageContentStream(doc, page);
                        contentStream.setFont(regularFont, fontSize);
                        yPosition = page.getMediaBox().getHeight() - margin;
                    }
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, yPosition);
                    contentStream.showText(cleanTextForPdf(wrappedLine));
                    contentStream.endText();
                    yPosition -= leading;
                }
                yPosition -= 5; // spacing between paragraphs
            }

            contentStream.close();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    private static List<String> wrapText(String text, PDFont font, float fontSize, float width) throws IOException {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            String potentialLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float lineSize = font.getStringWidth(potentialLine) / 1000f * fontSize;
            if (lineSize > width) {
                if (currentLine.length() > 0) {
                    result.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    result.add(potentialLine);
                    currentLine = new StringBuilder();
                }
            } else {
                currentLine = new StringBuilder(potentialLine);
            }
        }
        if (currentLine.length() > 0) {
            result.add(currentLine.toString());
        }
        if (text.trim().isEmpty() && result.isEmpty()) {
            result.add("");
        }
        return result;
    }

    private static String cleanTextForPdf(String text) {
        if (text == null) return "";
        return text.replace("\t", "    ")
                .replace("\r", "")
                .replaceAll("[\u2018\u2019]", "'")
                .replaceAll("[\u201C\u201D]", "\"")
                .replaceAll("[\u2013\u2014]", "-")
                .replaceAll("[^\\x20-\\x7E]", "?");
    }
}
