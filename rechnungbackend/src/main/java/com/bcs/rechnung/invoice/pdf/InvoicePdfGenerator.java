package com.bcs.rechnung.invoice.pdf;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.invoice.InvoiceEntity;
import com.bcs.rechnung.invoice.InvoiceItemEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Component
public class InvoicePdfGenerator {

    private static final float LEFT_MARGIN = 45.0f;
    private static final float RIGHT_MARGIN = 45.0f;
    private static final float TOP_MARGIN = 42.0f;
    private static final float BOTTOM_MARGIN = 48.0f;
    private static final float CONTENT_WIDTH =
        PDRectangle.A4.getWidth() - LEFT_MARGIN - RIGHT_MARGIN;

    private static final float NORMAL_FONT_SIZE = 9.0f;
    private static final float SMALL_FONT_SIZE = 8.0f;
    private static final float TITLE_FONT_SIZE = 18.0f;
    private static final float TABLE_HEADER_HEIGHT = 24.0f;
    private static final float TABLE_LINE_HEIGHT = 11.0f;
    private static final float TABLE_ROW_PADDING = 5.0f;

    private static final float POSITION_X = LEFT_MARGIN;
    private static final float DESCRIPTION_X = 74.0f;
    private static final float DESCRIPTION_WIDTH = 220.0f;
    private static final float QUANTITY_RIGHT_X = 350.0f;
    private static final float UNIT_X = 360.0f;
    private static final float PRICE_RIGHT_X = 462.0f;
    private static final float TOTAL_RIGHT_X =
        PDRectangle.A4.getWidth() - RIGHT_MARGIN;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generate(InvoiceEntity invoice) throws IOException {
        if (invoice == null) {
            throw new IllegalArgumentException("Die Rechnung darf nicht null sein.");
        }

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {

            PDFont regularFont = new PDType1Font(
                Standard14Fonts.FontName.HELVETICA
            );
            PDFont boldFont = new PDType1Font(
                Standard14Fonts.FontName.HELVETICA_BOLD
            );

            PageState state = openPage(document, regularFont, boldFont);
            writeFirstPageHeader(state, invoice);
            writeTableHeader(state);

            List<InvoiceItemEntity> items = invoice.getItems() == null
                ? Collections.emptyList()
                : invoice.getItems();

            for (InvoiceItemEntity item : items) {
                List<String> descriptionLines = wrapText(
                    buildDescription(item),
                    state.regularFont,
                    SMALL_FONT_SIZE,
                    DESCRIPTION_WIDTH
                );

                float rowHeight = Math.max(
                    TABLE_LINE_HEIGHT + (TABLE_ROW_PADDING * 2.0f),
                    (descriptionLines.size() * TABLE_LINE_HEIGHT)
                        + (TABLE_ROW_PADDING * 2.0f)
                );

                if (!hasSpace(state, rowHeight)) {
                    state.close();
                    state = openPage(document, regularFont, boldFont);
                    writeContinuationHeader(state, invoice);
                    writeTableHeader(state);
                }

                writeItemRow(state, item, descriptionLines, rowHeight);
            }

            if (!hasSpace(state, 190.0f)) {
                state.close();
                state = openPage(document, regularFont, boldFont);
                writeContinuationHeader(state, invoice);
            }

            writeTotals(state, invoice);

            if (!hasSpace(state, 115.0f)) {
                state.close();
                state = openPage(document, regularFont, boldFont);
                writeContinuationHeader(state, invoice);
            }

            writePaymentInformation(state, invoice);
            state = writeNotes(document, state, invoice, regularFont, boldFont);
            state.close();

            addPageNumbers(document, regularFont);
            document.save(output);
            return output.toByteArray();
        }
    }

    private PageState openPage(
        PDDocument document,
        PDFont regularFont,
        PDFont boldFont
    ) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream content = new PDPageContentStream(
            document,
            page
        );

        float startY = page.getMediaBox().getHeight() - TOP_MARGIN;
        return new PageState(page, content, regularFont, boldFont, startY);
    }

    private void writeFirstPageHeader(
        PageState state,
        InvoiceEntity invoice
    ) throws IOException {
        CompanyEntity company = invoice.getCompany();

        String companyName = company == null ? "" : safe(company.getName());
        String companyStreet = company == null ? "" : safe(company.getStreet());
        String companyLocation = company == null
            ? ""
            : joinNonBlank(company.getZipCode(), company.getCity());
        String companyCountry = company == null ? "" : safe(company.getCountry());

        writeText(
            state.content,
            state.boldFont,
            14.0f,
            LEFT_MARGIN,
            state.y,
            companyName
        );

        writeTextRightAligned(
            state.content,
            state.boldFont,
            TITLE_FONT_SIZE,
            TOTAL_RIGHT_X,
            state.y,
            "RECHNUNG"
        );

        state.moveDown(18.0f);
        writeOptionalLine(state, companyStreet);
        writeOptionalLine(state, companyLocation);
        writeOptionalLine(state, companyCountry);

        state.y = state.page.getMediaBox().getHeight() - 112.0f;
        drawLine(state, state.y, 0.7f, 0.55f);
        state.moveDown(22.0f);

        writeText(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            "Rechnungsempfänger"
        );

        state.moveDown(16.0f);
        writeText(
            state.content,
            state.boldFont,
            11.0f,
            LEFT_MARGIN,
            state.y,
            safe(invoice.getCustomerName())
        );
        state.moveDown(15.0f);
        writeOptionalLine(state, invoice.getCustomerStreet());
        writeOptionalLine(
            state,
            joinNonBlank(
                invoice.getCustomerZipCode(),
                invoice.getCustomerCity()
            )
        );
        writeOptionalLine(state, invoice.getCustomerCountry());

        float labelX = 350.0f;
        float valueRightX = TOTAL_RIGHT_X;
        float informationY = state.page.getMediaBox().getHeight() - 150.0f;

        writeLabelValue(
            state,
            labelX,
            valueRightX,
            informationY,
            "Rechnungsnummer:",
            invoiceNumber(invoice)
        );
        informationY -= 14.0f;
        writeLabelValue(
            state,
            labelX,
            valueRightX,
            informationY,
            "Rechnungsdatum:",
            formatDate(invoice.getInvoiceDate())
        );
        informationY -= 14.0f;
        writeLabelValue(
            state,
            labelX,
            valueRightX,
            informationY,
            "Leistungsdatum:",
            formatDate(invoice.getServiceDate())
        );
        informationY -= 14.0f;
        writeLabelValue(
            state,
            labelX,
            valueRightX,
            informationY,
            "Fällig am:",
            formatDate(invoice.getDueDate())
        );
        informationY -= 14.0f;
        writeLabelValue(
            state,
            labelX,
            valueRightX,
            informationY,
            "Kundennummer:",
            safe(invoice.getCustomerNumber())
        );

        state.y = Math.min(state.y, informationY) - 28.0f;
    }

    private void writeContinuationHeader(
        PageState state,
        InvoiceEntity invoice
    ) throws IOException {
        writeText(
            state.content,
            state.boldFont,
            12.0f,
            LEFT_MARGIN,
            state.y,
            "Rechnung " + invoiceNumber(invoice)
        );

        writeTextRightAligned(
            state.content,
            state.regularFont,
            NORMAL_FONT_SIZE,
            TOTAL_RIGHT_X,
            state.y,
            "Fortsetzung"
        );

        state.moveDown(18.0f);
        drawLine(state, state.y, 0.7f, 0.55f);
        state.moveDown(18.0f);
    }

    private void writeTableHeader(PageState state) throws IOException {
        float topY = state.y;

        state.content.setNonStrokingColor(0.90f, 0.92f, 0.95f);
        state.content.addRect(
            LEFT_MARGIN,
            topY - TABLE_HEADER_HEIGHT,
            CONTENT_WIDTH,
            TABLE_HEADER_HEIGHT
        );
        state.content.fill();
        state.content.setNonStrokingColor(0.0f, 0.0f, 0.0f);

        float textY = topY - 16.0f;
        writeText(state.content, state.boldFont, SMALL_FONT_SIZE,
            POSITION_X, textY, "Pos.");
        writeText(state.content, state.boldFont, SMALL_FONT_SIZE,
            DESCRIPTION_X, textY, "Beschreibung");
        writeTextRightAligned(state.content, state.boldFont, SMALL_FONT_SIZE,
            QUANTITY_RIGHT_X, textY, "Menge");
        writeText(state.content, state.boldFont, SMALL_FONT_SIZE,
            UNIT_X, textY, "Einheit");
        writeTextRightAligned(state.content, state.boldFont, SMALL_FONT_SIZE,
            PRICE_RIGHT_X, textY, "Einzelpreis");
        writeTextRightAligned(state.content, state.boldFont, SMALL_FONT_SIZE,
            TOTAL_RIGHT_X, textY, "Gesamt");

        state.y = topY - TABLE_HEADER_HEIGHT;
    }

    private void writeItemRow(
        PageState state,
        InvoiceItemEntity item,
        List<String> descriptionLines,
        float rowHeight
    ) throws IOException {
        float rowTop = state.y;
        float firstLineY = rowTop - TABLE_ROW_PADDING - SMALL_FONT_SIZE;

        writeText(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            POSITION_X,
            firstLineY,
            item.getPositionNumber() == null
                ? ""
                : String.valueOf(item.getPositionNumber())
        );

        float descriptionY = firstLineY;
        for (String line : descriptionLines) {
            writeText(
                state.content,
                state.regularFont,
                SMALL_FONT_SIZE,
                DESCRIPTION_X,
                descriptionY,
                line
            );
            descriptionY -= TABLE_LINE_HEIGHT;
        }

        writeTextRightAligned(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            QUANTITY_RIGHT_X,
            firstLineY,
            formatQuantity(item.getQuantity())
        );
        writeText(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            UNIT_X,
            firstLineY,
            safe(item.getUnit())
        );
        writeTextRightAligned(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            PRICE_RIGHT_X,
            firstLineY,
            formatMoney(item.getUnitPriceNet())
        );
        writeTextRightAligned(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            TOTAL_RIGHT_X,
            firstLineY,
            formatMoney(item.getLineNet())
        );

        state.y = rowTop - rowHeight;
        drawLine(state, state.y, 0.3f, 0.82f);
    }

    private void writeTotals(
        PageState state,
        InvoiceEntity invoice
    ) throws IOException {
        state.moveDown(20.0f);

        float labelX = 360.0f;
        float valueRightX = TOTAL_RIGHT_X;

        writeMoneyRow(
            state,
            labelX,
            valueRightX,
            "Nettosumme:",
            invoice.getSubtotalNet(),
            false
        );
        state.moveDown(17.0f);

        writeMoneyRow(
            state,
            labelX,
            valueRightX,
            "Umsatzsteuer:",
            invoice.getTaxAmount(),
            false
        );
        state.moveDown(12.0f);

        drawLine(state, state.y, 0.8f, 0.55f);
        state.moveDown(19.0f);

        writeMoneyRow(
            state,
            labelX,
            valueRightX,
            "Gesamtbetrag:",
            invoice.getTotalGross(),
            true
        );
        state.moveDown(30.0f);
    }

    private void writePaymentInformation(
        PageState state,
        InvoiceEntity invoice
    ) throws IOException {
        writeText(
            state.content,
            state.boldFont,
            10.0f,
            LEFT_MARGIN,
            state.y,
            "Zahlungsinformationen"
        );
        state.moveDown(17.0f);

        writeText(
            state.content,
            state.regularFont,
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            "Bitte überweisen Sie den Rechnungsbetrag bis zum "
                + formatDate(invoice.getDueDate())
                + "."
        );
        state.moveDown(15.0f);

        writeText(state.content, state.regularFont, NORMAL_FONT_SIZE,
            LEFT_MARGIN, state.y,
            "Bank: " + safe(invoice.getBankName()));
        state.moveDown(14.0f);

        writeText(state.content, state.regularFont, NORMAL_FONT_SIZE,
            LEFT_MARGIN, state.y,
            "IBAN: " + safe(invoice.getIban()));
        state.moveDown(14.0f);

        writeText(state.content, state.regularFont, NORMAL_FONT_SIZE,
            LEFT_MARGIN, state.y,
            "BIC: " + safe(invoice.getBic()));

        BigDecimal discountPercent = zeroIfNull(invoice.getDiscountPercent());
        Integer discountDays = invoice.getDiscountDays();

        if (discountPercent.compareTo(BigDecimal.ZERO) > 0
            && discountDays != null
            && discountDays > 0) {
            state.moveDown(14.0f);
            writeText(
                state.content,
                state.regularFont,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                state.y,
                formatDecimal(discountPercent)
                    + " % Skonto bei Zahlung innerhalb von "
                    + discountDays
                    + " Tagen."
            );
        }

        state.moveDown(24.0f);
    }

    private PageState writeNotes(
        PDDocument document,
        PageState state,
        InvoiceEntity invoice,
        PDFont regularFont,
        PDFont boldFont
    ) throws IOException {
        String notes = normalize(invoice.getNotes());
        if (notes == null) {
            return state;
        }

        List<String> lines = wrapText(
            notes,
            regularFont,
            NORMAL_FONT_SIZE,
            CONTENT_WIDTH
        );

        float requiredHeaderHeight = 30.0f;
        if (!hasSpace(state, requiredHeaderHeight)) {
            state.close();
            state = openPage(document, regularFont, boldFont);
            writeContinuationHeader(state, invoice);
        }

        writeText(
            state.content,
            state.boldFont,
            10.0f,
            LEFT_MARGIN,
            state.y,
            "Anmerkungen"
        );
        state.moveDown(17.0f);

        for (String line : lines) {
            if (!hasSpace(state, 14.0f)) {
                state.close();
                state = openPage(document, regularFont, boldFont);
                writeContinuationHeader(state, invoice);
            }

            writeText(
                state.content,
                state.regularFont,
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                state.y,
                line
            );
            state.moveDown(13.0f);
        }

        return state;
    }

    private void addPageNumbers(
        PDDocument document,
        PDFont font
    ) throws IOException {
        int pageCount = document.getNumberOfPages();

        for (int index = 0; index < pageCount; index++) {
            PDPage page = document.getPage(index);

            try (PDPageContentStream content = new PDPageContentStream(
                document,
                page,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true
            )) {
                String text = "Seite " + (index + 1) + " von " + pageCount;
                float width = textWidth(font, SMALL_FONT_SIZE, text);

                writeText(
                    content,
                    font,
                    SMALL_FONT_SIZE,
                    page.getMediaBox().getWidth() - RIGHT_MARGIN - width,
                    24.0f,
                    text
                );
            }
        }
    }

    private boolean hasSpace(PageState state, float requiredHeight) {
        return state.y - requiredHeight >= BOTTOM_MARGIN;
    }

    private void writeLabelValue(
        PageState state,
        float labelX,
        float valueRightX,
        float y,
        String label,
        String value
    ) throws IOException {
        writeText(
            state.content,
            state.boldFont,
            SMALL_FONT_SIZE,
            labelX,
            y,
            label
        );
        writeTextRightAligned(
            state.content,
            state.regularFont,
            SMALL_FONT_SIZE,
            valueRightX,
            y,
            value
        );
    }

    private void writeMoneyRow(
        PageState state,
        float labelX,
        float valueRightX,
        String label,
        BigDecimal value,
        boolean bold
    ) throws IOException {
        PDFont font = bold ? state.boldFont : state.regularFont;
        float fontSize = bold ? 11.0f : NORMAL_FONT_SIZE;

        writeText(
            state.content,
            font,
            fontSize,
            labelX,
            state.y,
            label
        );
        writeTextRightAligned(
            state.content,
            font,
            fontSize,
            valueRightX,
            state.y,
            formatMoney(value) + " EUR"
        );
    }

    private void writeOptionalLine(PageState state, String value)
        throws IOException {
        String normalized = normalize(value);
        if (normalized == null) {
            return;
        }

        writeText(
            state.content,
            state.regularFont,
            NORMAL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            normalized
        );
        state.moveDown(13.0f);
    }

    private void writeText(
        PDPageContentStream content,
        PDFont font,
        float fontSize,
        float x,
        float y,
        String value
    ) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(sanitize(value));
        content.endText();
    }

    private void writeTextRightAligned(
        PDPageContentStream content,
        PDFont font,
        float fontSize,
        float rightX,
        float y,
        String value
    ) throws IOException {
        String sanitized = sanitize(value);
        float width = textWidth(font, fontSize, sanitized);
        writeText(content, font, fontSize, rightX - width, y, sanitized);
    }

    private float textWidth(
        PDFont font,
        float fontSize,
        String value
    ) throws IOException {
        return font.getStringWidth(sanitize(value)) / 1000.0f * fontSize;
    }

    private void drawLine(
        PageState state,
        float y,
        float lineWidth,
        float gray
    ) throws IOException {
        state.content.setStrokingColor(gray, gray, gray);
        state.content.setLineWidth(lineWidth);
        state.content.moveTo(LEFT_MARGIN, y);
        state.content.lineTo(LEFT_MARGIN + CONTENT_WIDTH, y);
        state.content.stroke();
    }

    private List<String> wrapText(
        String value,
        PDFont font,
        float fontSize,
        float maximumWidth
    ) throws IOException {
        String normalized = normalize(value);
        if (normalized == null) {
            return Collections.singletonList("");
        }

        List<String> lines = new ArrayList<String>();
        String[] paragraphs = normalized.split("\\R");

        for (String paragraph : paragraphs) {
            String trimmedParagraph = paragraph.trim();
            if (trimmedParagraph.isEmpty()) {
                lines.add("");
                continue;
            }

            String[] words = trimmedParagraph.split("\\s+");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String candidate = currentLine.length() == 0
                    ? word
                    : currentLine.toString() + " " + word;

                if (textWidth(font, fontSize, candidate) <= maximumWidth) {
                    currentLine.setLength(0);
                    currentLine.append(candidate);
                } else {
                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine.setLength(0);
                    }

                    if (textWidth(font, fontSize, word) <= maximumWidth) {
                        currentLine.append(word);
                    } else {
                        splitLongWord(
                            word,
                            font,
                            fontSize,
                            maximumWidth,
                            lines,
                            currentLine
                        );
                    }
                }
            }

            if (currentLine.length() > 0) {
                lines.add(currentLine.toString());
            }
        }

        return lines.isEmpty() ? Collections.singletonList("") : lines;
    }

    private void splitLongWord(
        String word,
        PDFont font,
        float fontSize,
        float maximumWidth,
        List<String> completedLines,
        StringBuilder remainder
    ) throws IOException {
        StringBuilder part = new StringBuilder();

        for (int index = 0; index < word.length(); index++) {
            char character = word.charAt(index);
            String candidate = part.toString() + character;

            if (textWidth(font, fontSize, candidate) <= maximumWidth) {
                part.append(character);
            } else {
                if (part.length() > 0) {
                    completedLines.add(part.toString());
                }
                part.setLength(0);
                part.append(character);
            }
        }

        remainder.append(part);
    }

    private String buildDescription(InvoiceItemEntity item) {
        String productNumber = normalize(item.getProductNumber());
        String description = normalize(item.getDescription());

        if (productNumber == null) {
            return safe(description);
        }
        if (description == null) {
            return productNumber;
        }
        return productNumber + " - " + description;
    }

    private String invoiceNumber(InvoiceEntity invoice) {
        String value = normalize(invoice.getInvoiceNumber());
        return value == null ? "ENTWURF" : value;
    }

    private String formatDate(LocalDate value) {
        return value == null ? "" : DATE_FORMAT.format(value);
    }

    private String formatMoney(BigDecimal value) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMANY);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        BigDecimal normalized = zeroIfNull(value).setScale(
            2,
            RoundingMode.HALF_UP
        );
        return formatter.format(normalized);
    }

    private String formatQuantity(BigDecimal value) {
        return value == null ? "" : formatDecimal(value);
    }

    private String formatDecimal(BigDecimal value) {
        return value.stripTrailingZeros()
            .toPlainString()
            .replace('.', ',');
    }

    private String joinNonBlank(String first, String second) {
        String firstValue = normalize(first);
        String secondValue = normalize(second);

        if (firstValue == null) {
            return safe(secondValue);
        }
        if (secondValue == null) {
            return firstValue;
        }
        return firstValue + " " + secondValue;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String safe(String value) {
        String normalized = normalize(value);
        return normalized == null ? "" : normalized;
    }

    private String sanitize(String value) {
        return safe(value)
            .replace("€", "EUR")
            .replace("–", "-")
            .replace("—", "-")
            .replace("„", "\"")
            .replace("“", "\"")
            .replace("”", "\"")
            .replace("’", "'")
            .replace("…", "...");
    }

    private BigDecimal zeroIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private static final class PageState {

        private final PDPage page;
        private final PDPageContentStream content;
        private final PDFont regularFont;
        private final PDFont boldFont;
        private float y;

        private PageState(
            PDPage page,
            PDPageContentStream content,
            PDFont regularFont,
            PDFont boldFont,
            float y
        ) {
            this.page = page;
            this.content = content;
            this.regularFont = regularFont;
            this.boldFont = boldFont;
            this.y = y;
        }

        private void moveDown(float distance) {
            y -= distance;
        }

        private void close() throws IOException {
            content.close();
        }
    }
}
