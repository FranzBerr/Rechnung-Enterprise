package com.bcs.rechnung.deliverynote.pdf;

import com.bcs.rechnung.company.CompanyEntity;
import com.bcs.rechnung.customer.CustomerEntity;
import com.bcs.rechnung.deliverynote.DeliveryNoteEntity;
import com.bcs.rechnung.deliverynote.DeliveryNoteItemEntity;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DeliveryNotePdfGeneratorImpl
    implements DeliveryNotePdfGenerator {

    private static final PDRectangle PAGE_SIZE =
        PDRectangle.A4;

    private static final float LEFT_MARGIN = 45.0f;
    private static final float RIGHT_MARGIN = 45.0f;
    private static final float TOP_MARGIN = 42.0f;
    private static final float BOTTOM_MARGIN = 48.0f;

    private static final float CONTENT_WIDTH =
        PAGE_SIZE.getWidth()
            - LEFT_MARGIN
            - RIGHT_MARGIN;

    private static final float RIGHT_X =
        PAGE_SIZE.getWidth()
            - RIGHT_MARGIN;

    private static final float NORMAL_FONT_SIZE = 9.0f;
    private static final float SMALL_FONT_SIZE = 8.0f;
    private static final float TITLE_FONT_SIZE = 18.0f;

    private static final float TABLE_HEADER_HEIGHT = 24.0f;
    private static final float TABLE_LINE_HEIGHT = 11.0f;
    private static final float TABLE_ROW_PADDING = 5.0f;

    private static final float POSITION_X = LEFT_MARGIN;
    private static final float DESCRIPTION_X = 74.0f;
    private static final float DESCRIPTION_WIDTH = 335.0f;
    private static final float QUANTITY_RIGHT_X = 465.0f;
    private static final float UNIT_X = 480.0f;

    private static final float SIGNATURE_BLOCK_HEIGHT = 205.0f;
    private static final float SIGNATURE_IMAGE_HEIGHT = 38.0f;

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public byte[] generate(
        DeliveryNoteEntity deliveryNote
    ) throws IOException {

        validateDeliveryNote(deliveryNote);

        try (
            PDDocument document = new PDDocument();
            ByteArrayOutputStream output =
                new ByteArrayOutputStream()
        ) {
            PdfFonts fonts = createFonts();
            PageState state = openPage(document, fonts);

            writeFirstPageHeader(
                state,
                deliveryNote
            );

            writeTableHeader(state);

            state = writeItems(
                document,
                state,
                deliveryNote,
                fonts
            );

            state = writeShippingInformation(
                document,
                state,
                deliveryNote,
                fonts
            );

            state = writeNotes(
                document,
                state,
                deliveryNote,
                fonts
            );

            state = newPageIfNeeded(
                document,
                state,
                deliveryNote,
                fonts,
                SIGNATURE_BLOCK_HEIGHT,
                false
            );

            writeSignatures(
                document,
                state,
                deliveryNote
            );

            state.close();

            addPageNumbers(
                document,
                fonts.regular()
            );

            document.save(output);

            return output.toByteArray();
        }
    }

    private PdfFonts createFonts() {
        PDFont regularFont = new PDType1Font(
            Standard14Fonts.FontName.HELVETICA
        );

        PDFont boldFont = new PDType1Font(
            Standard14Fonts.FontName.HELVETICA_BOLD
        );

        return new PdfFonts(
            regularFont,
            boldFont
        );
    }

    private PageState openPage(
        PDDocument document,
        PdfFonts fonts
    ) throws IOException {

        PDPage page = new PDPage(PAGE_SIZE);
        document.addPage(page);

        PDPageContentStream content =
            new PDPageContentStream(
                document,
                page
            );

        return new PageState(
            page,
            content,
            fonts,
            PAGE_SIZE.getHeight() - TOP_MARGIN
        );
    }

    private PageState newPage(
        PDDocument document,
        PageState currentState,
        DeliveryNoteEntity deliveryNote,
        PdfFonts fonts,
        boolean includeTableHeader
    ) throws IOException {

        currentState.close();

        PageState newState =
            openPage(document, fonts);

        writeContinuationHeader(
            newState,
            deliveryNote
        );

        if (includeTableHeader) {
            writeTableHeader(newState);
        }

        return newState;
    }

    private PageState newPageIfNeeded(
        PDDocument document,
        PageState state,
        DeliveryNoteEntity deliveryNote,
        PdfFonts fonts,
        float requiredHeight,
        boolean includeTableHeader
    ) throws IOException {

        if (hasSpace(state, requiredHeight)) {
            return state;
        }

        return newPage(
            document,
            state,
            deliveryNote,
            fonts,
            includeTableHeader
        );
    }

    private boolean hasSpace(
        PageState state,
        float requiredHeight
    ) {
        return state.y - requiredHeight
            >= BOTTOM_MARGIN;
    }

    private void writeFirstPageHeader(
        PageState state,
        DeliveryNoteEntity deliveryNote
    ) throws IOException {

        CompanyEntity company =
            resolveCompany(deliveryNote);

        writeCompanyHeader(
            state,
            company
        );

        state.y =
            PAGE_SIZE.getHeight() - 112.0f;

        drawLine(
            state,
            state.y,
            0.7f,
            0.55f
        );

        state.moveDown(22.0f);

        float addressBottom =
            writeDeliveryAddress(
                state,
                deliveryNote
            );

        float informationBottom =
            writeDocumentInformation(
                state,
                deliveryNote
            );

        state.y =
            Math.min(
                addressBottom,
                informationBottom
            ) - 28.0f;
    }

    private void writeCompanyHeader(
        PageState state,
        CompanyEntity company
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            14.0f,
            LEFT_MARGIN,
            state.y,
            company == null
                ? ""
                : company.getName()
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            TITLE_FONT_SIZE,
            RIGHT_X,
            state.y,
            "LIEFERSCHEIN"
        );

        state.moveDown(18.0f);

        if (company == null) {
            return;
        }

        writeOptionalLine(
            state,
            company.getStreet()
        );

        writeOptionalLine(
            state,
            joinNonBlank(
                company.getZipCode(),
                company.getCity()
            )
        );

        writeOptionalLine(
            state,
            company.getCountry()
        );
    }

    private float writeDeliveryAddress(
        PageState state,
        DeliveryNoteEntity deliveryNote
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            "Lieferempfänger"
        );

        state.moveDown(16.0f);

        writeText(
            state.content,
            state.fonts.bold(),
            11.0f,
            LEFT_MARGIN,
            state.y,
            deliveryNote.getDeliveryName()
        );

        state.moveDown(15.0f);

        writeOptionalLine(
            state,
            deliveryNote.getDeliveryDepartment()
        );

        writeOptionalLine(
            state,
            deliveryNote.getDeliveryContactPerson()
        );

        writeOptionalLine(
            state,
            deliveryNote.getDeliveryStreet()
        );

        writeOptionalLine(
            state,
            joinNonBlank(
                deliveryNote.getDeliveryZipCode(),
                deliveryNote.getDeliveryCity()
            )
        );

        writeOptionalLine(
            state,
            deliveryNote.getDeliveryCountry()
        );

        return state.y;
    }

    private float writeDocumentInformation(
        PageState state,
        DeliveryNoteEntity deliveryNote
    ) throws IOException {

        float labelX = 350.0f;
        float y = PAGE_SIZE.getHeight() - 150.0f;

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Lieferscheinnummer:",
            deliveryNoteNumber(deliveryNote)
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Lieferdatum:",
            formatDate(
                deliveryNote.getDeliveryDate()
            )
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Kundennummer:",
            customerNumber(deliveryNote)
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Auftragsreferenz:",
            deliveryNote.getOrderReference()
        );

        y = writeLabelValue(
            state,
            labelX,
            y,
            "Versandart:",
            deliveryNote.getShippingMethod()
        );

        return writeLabelValue(
            state,
            labelX,
            y,
            "Trackingnummer:",
            deliveryNote.getTrackingNumber()
        );
    }

    private void writeContinuationHeader(
        PageState state,
        DeliveryNoteEntity deliveryNote
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            12.0f,
            LEFT_MARGIN,
            state.y,
            "Lieferschein "
                + deliveryNoteNumber(deliveryNote)
        );

        writeTextRightAligned(
            state.content,
            state.fonts.regular(),
            NORMAL_FONT_SIZE,
            RIGHT_X,
            state.y,
            "Fortsetzung"
        );

        state.moveDown(18.0f);

        drawLine(
            state,
            state.y,
            0.7f,
            0.55f
        );

        state.moveDown(18.0f);
    }

    private void writeTableHeader(
        PageState state
    ) throws IOException {

        float topY = state.y;

        state.content.setNonStrokingColor(
            0.90f,
            0.92f,
            0.95f
        );

        state.content.addRect(
            LEFT_MARGIN,
            topY - TABLE_HEADER_HEIGHT,
            CONTENT_WIDTH,
            TABLE_HEADER_HEIGHT
        );

        state.content.fill();

        state.content.setNonStrokingColor(
            0.0f,
            0.0f,
            0.0f
        );

        float y = topY - 16.0f;

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            POSITION_X,
            y,
            "Pos."
        );

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            DESCRIPTION_X,
            y,
            "Beschreibung"
        );

        writeTextRightAligned(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            QUANTITY_RIGHT_X,
            y,
            "Menge"
        );

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            UNIT_X,
            y,
            "Einheit"
        );

        state.y =
            topY - TABLE_HEADER_HEIGHT;
    }

    private PageState writeItems(
        PDDocument document,
        PageState state,
        DeliveryNoteEntity deliveryNote,
        PdfFonts fonts
    ) throws IOException {

        List<DeliveryNoteItemEntity> items =
            deliveryNote.getItems() == null
                ? Collections.emptyList()
                : deliveryNote.getItems();

        if (items.isEmpty()) {
            state = newPageIfNeeded(
                document,
                state,
                deliveryNote,
                fonts,
                48.0f,
                true
            );

            state.moveDown(18.0f);

            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                DESCRIPTION_X,
                state.y,
                "Keine Positionen vorhanden."
            );

            state.moveDown(30.0f);

            return state;
        }

        for (DeliveryNoteItemEntity item : items) {
            state = writeItem(
                document,
                state,
                deliveryNote,
                item,
                fonts
            );
        }

        return state;
    }

    private PageState writeItem(
        PDDocument document,
        PageState state,
        DeliveryNoteEntity deliveryNote,
        DeliveryNoteItemEntity item,
        PdfFonts fonts
    ) throws IOException {

        List<String> lines = wrapText(
            buildDescription(item),
            fonts.regular(),
            SMALL_FONT_SIZE,
            DESCRIPTION_WIDTH
        );

        int index = 0;
        boolean firstPart = true;

        while (index < lines.size()) {
            int availableLines =
                calculateAvailableLines(state);

            if (availableLines <= 0) {
                state = newPage(
                    document,
                    state,
                    deliveryNote,
                    fonts,
                    true
                );

                availableLines =
                    calculateAvailableLines(state);
            }

            int lineCount = Math.min(
                lines.size() - index,
                availableLines
            );

            List<String> visibleLines =
                new ArrayList<>(
                    lines.subList(
                        index,
                        index + lineCount
                    )
                );

            writeItemRow(
                state,
                item,
                visibleLines,
                calculateRowHeight(lineCount),
                firstPart
            );

            index += lineCount;
            firstPart = false;

            if (index < lines.size()) {
                state = newPage(
                    document,
                    state,
                    deliveryNote,
                    fonts,
                    true
                );
            }
        }

        return state;
    }

    private int calculateAvailableLines(
        PageState state
    ) {
        float availableHeight =
            state.y
                - BOTTOM_MARGIN
                - TABLE_ROW_PADDING * 2.0f;

        if (availableHeight < TABLE_LINE_HEIGHT) {
            return 0;
        }

        return Math.max(
            1,
            (int) Math.floor(
                availableHeight
                    / TABLE_LINE_HEIGHT
            )
        );
    }

    private float calculateRowHeight(
        int lineCount
    ) {
        return Math.max(
            TABLE_LINE_HEIGHT
                + TABLE_ROW_PADDING * 2.0f,
            lineCount
                * TABLE_LINE_HEIGHT
                + TABLE_ROW_PADDING * 2.0f
        );
    }

    private void writeItemRow(
        PageState state,
        DeliveryNoteItemEntity item,
        List<String> lines,
        float rowHeight,
        boolean firstPart
    ) throws IOException {

        float rowTop = state.y;

        float y =
            rowTop
                - TABLE_ROW_PADDING
                - SMALL_FONT_SIZE;

        if (firstPart) {
            writeText(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                POSITION_X,
                y,
                item.getPositionNumber() == null
                    ? ""
                    : item.getPositionNumber().toString()
            );
        }

        float textY = y;

        for (String line : lines) {
            writeText(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                DESCRIPTION_X,
                textY,
                line
            );

            textY -= TABLE_LINE_HEIGHT;
        }

        if (firstPart) {
            writeTextRightAligned(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                QUANTITY_RIGHT_X,
                y,
                formatQuantity(
                    item.getQuantity()
                )
            );

            writeText(
                state.content,
                state.fonts.regular(),
                SMALL_FONT_SIZE,
                UNIT_X,
                y,
                item.getUnit()
            );
        }

        state.y =
            rowTop - rowHeight;

        drawLine(
            state,
            state.y,
            0.3f,
            0.82f
        );
    }

    private PageState writeShippingInformation(
        PDDocument document,
        PageState state,
        DeliveryNoteEntity deliveryNote,
        PdfFonts fonts
    ) throws IOException {

        List<LabelValue> entries =
            shippingEntries(deliveryNote);

        if (entries.isEmpty()) {
            return state;
        }

        float requiredHeight =
            55.0f
                + entries.size() * 15.0f;

        state = newPageIfNeeded(
            document,
            state,
            deliveryNote,
            fonts,
            requiredHeight,
            false
        );

        state.moveDown(22.0f);

        writeText(
            state.content,
            state.fonts.bold(),
            10.0f,
            LEFT_MARGIN,
            state.y,
            "Versanddaten"
        );

        state.moveDown(17.0f);

        float valueX =
            LEFT_MARGIN + 105.0f;

        for (LabelValue entry : entries) {
            writeText(
                state.content,
                state.fonts.bold(),
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                state.y,
                entry.label()
            );

            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                valueX,
                state.y,
                entry.value()
            );

            state.moveDown(15.0f);
        }

        state.moveDown(10.0f);

        return state;
    }

    private List<LabelValue> shippingEntries(
        DeliveryNoteEntity deliveryNote
    ) {
        List<LabelValue> entries =
            new ArrayList<>();

        addEntry(
            entries,
            "Auftragsreferenz:",
            deliveryNote.getOrderReference()
        );

        addEntry(
            entries,
            "Versandart:",
            deliveryNote.getShippingMethod()
        );

        addEntry(
            entries,
            "Trackingnummer:",
            deliveryNote.getTrackingNumber()
        );

        return entries;
    }

    private void addEntry(
        List<LabelValue> entries,
        String label,
        String value
    ) {
        String normalized =
            normalize(value);

        if (normalized != null) {
            entries.add(
                new LabelValue(
                    label,
                    normalized
                )
            );
        }
    }

    private PageState writeNotes(
        PDDocument document,
        PageState state,
        DeliveryNoteEntity deliveryNote,
        PdfFonts fonts
    ) throws IOException {

        String notes =
            normalize(deliveryNote.getNotes());

        if (notes == null) {
            return state;
        }

        List<String> lines = wrapText(
            notes,
            fonts.regular(),
            NORMAL_FONT_SIZE,
            CONTENT_WIDTH
        );

        state = newPageIfNeeded(
            document,
            state,
            deliveryNote,
            fonts,
            30.0f,
            false
        );

        writeNotesHeading(
            state,
            false
        );

        for (String line : lines) {
            if (!hasSpace(state, 14.0f)) {
                state = newPage(
                    document,
                    state,
                    deliveryNote,
                    fonts,
                    false
                );

                writeNotesHeading(
                    state,
                    true
                );
            }

            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                LEFT_MARGIN,
                state.y,
                line
            );

            state.moveDown(13.0f);
        }

        state.moveDown(15.0f);

        return state;
    }

    private void writeNotesHeading(
        PageState state,
        boolean continuation
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            10.0f,
            LEFT_MARGIN,
            state.y,
            continuation
                ? "Anmerkungen - Fortsetzung"
                : "Anmerkungen"
        );

        state.moveDown(17.0f);
    }

    private void writeSignatures(
        PDDocument document,
        PageState state,
        DeliveryNoteEntity deliveryNote
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            LEFT_MARGIN,
            state.y,
            "Bitte prüfen Sie die Lieferung bei Erhalt "
                + "auf Vollständigkeit und sichtbare Schäden."
        );

        state.moveDown(28.0f);

        float columnGap = 30.0f;

        float columnWidth =
            (CONTENT_WIDTH - columnGap) / 2.0f;

        float leftX = LEFT_MARGIN;

        float rightX =
            LEFT_MARGIN
                + columnWidth
                + columnGap;

        writeText(
            state.content,
            state.fonts.bold(),
            10.0f,
            leftX,
            state.y,
            "Auslieferung"
        );

        writeText(
            state.content,
            state.fonts.bold(),
            10.0f,
            rightX,
            state.y,
            "Warenempfang"
        );

        state.moveDown(27.0f);

        writeSignatureValueRow(
            state,
            leftX,
            rightX,
            columnWidth,
            "Datum",
            formatDate(
                deliveryNote.getDeliveryDateSigned()
            ),
            formatDate(
                deliveryNote.getReceiptDateSigned()
            )
        );

        state.moveDown(34.0f);

        writeSignatureValueRow(
            state,
            leftX,
            rightX,
            columnWidth,
            "Name",
            deliveryNote.getDeliveryPerson(),
            deliveryNote.getReceiptPerson()
        );

        state.moveDown(44.0f);

        writeSignatureImageRow(
            document,
            state,
            leftX,
            rightX,
            columnWidth,
            deliveryNote.getDeliverySignature(),
            deliveryNote.getReceiptSignature()
        );
    }

    private void writeSignatureValueRow(
        PageState state,
        float leftX,
        float rightX,
        float width,
        String label,
        String leftValue,
        String rightValue
    ) throws IOException {

        writeSignatureValue(
            state,
            leftX,
            width,
            label,
            leftValue
        );

        writeSignatureValue(
            state,
            rightX,
            width,
            label,
            rightValue
        );
    }

    private void writeSignatureValue(
        PageState state,
        float x,
        float width,
        String label,
        String value
    ) throws IOException {

        String normalized =
            normalize(value);

        if (normalized != null) {
            writeText(
                state.content,
                state.fonts.regular(),
                NORMAL_FONT_SIZE,
                x,
                state.y + 4.0f,
                normalized
            );
        }

        drawInputLine(
            state,
            x,
            width,
            state.y
        );

        writeText(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            x,
            state.y - 12.0f,
            label
        );
    }

    private void writeSignatureImageRow(
        PDDocument document,
        PageState state,
        float leftX,
        float rightX,
        float width,
        byte[] deliverySignature,
        byte[] receiptSignature
    ) throws IOException {

        drawSignatureImage(
            document,
            state,
            leftX,
            width,
            deliverySignature
        );

        drawSignatureImage(
            document,
            state,
            rightX,
            width,
            receiptSignature
        );
    }

    private void drawSignatureImage(
        PDDocument document,
        PageState state,
        float x,
        float width,
        byte[] signature
    ) throws IOException {

        if (
            signature != null
                && signature.length > 0
        ) {
            PDImageXObject image =
                createSignatureImage(
                    document,
                    signature
                );

            ImageSize size =
                calculateImageSize(
                    image,
                    width,
                    SIGNATURE_IMAGE_HEIGHT
                );

            float imageX = x;

            float imageY =
                state.y + 4.0f;

            state.content.drawImage(
                image,
                imageX,
                imageY,
                size.width(),
                size.height()
            );
        }

        drawInputLine(
            state,
            x,
            width,
            state.y
        );

        writeText(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            x,
            state.y - 12.0f,
            "Unterschrift"
        );
    }

    private PDImageXObject createSignatureImage(
        PDDocument document,
        byte[] signature
    ) throws IOException {

        try {
            return PDImageXObject
                .createFromByteArray(
                    document,
                    signature,
                    "signature"
                );

        } catch (
            IllegalArgumentException exception
        ) {
            throw new IOException(
                "Die gespeicherten Signaturdaten "
                    + "sind kein unterstütztes Bildformat.",
                exception
            );
        }
    }

    private ImageSize calculateImageSize(
        PDImageXObject image,
        float maximumWidth,
        float maximumHeight
    ) {

        if (
            image.getWidth() <= 0
                || image.getHeight() <= 0
        ) {
            return new ImageSize(
                maximumWidth,
                maximumHeight
            );
        }

        float widthRatio =
            maximumWidth / image.getWidth();

        float heightRatio =
            maximumHeight / image.getHeight();

        float scale =
            Math.min(
                widthRatio,
                heightRatio
            );

        return new ImageSize(
            image.getWidth() * scale,
            image.getHeight() * scale
        );
    }

    private void drawInputLine(
        PageState state,
        float x,
        float width,
        float y
    ) throws IOException {

        state.content.setStrokingColor(
            0.55f,
            0.55f,
            0.55f
        );

        state.content.setLineWidth(0.7f);

        state.content.moveTo(x, y);

        state.content.lineTo(
            x + width,
            y
        );

        state.content.stroke();
    }

    private void addPageNumbers(
        PDDocument document,
        PDFont font
    ) throws IOException {

        int pageCount =
            document.getNumberOfPages();

        for (
            int index = 0;
            index < pageCount;
            index++
        ) {
            PDPage page =
                document.getPage(index);

            try (
                PDPageContentStream content =
                    new PDPageContentStream(
                        document,
                        page,
                        PDPageContentStream
                            .AppendMode
                            .APPEND,
                        true,
                        true
                    )
            ) {
                writeTextRightAligned(
                    content,
                    font,
                    SMALL_FONT_SIZE,
                    page.getMediaBox().getWidth()
                        - RIGHT_MARGIN,
                    24.0f,
                    "Seite "
                        + (index + 1)
                        + " von "
                        + pageCount
                );
            }
        }
    }

    private float writeLabelValue(
        PageState state,
        float labelX,
        float y,
        String label,
        String value
    ) throws IOException {

        writeText(
            state.content,
            state.fonts.bold(),
            SMALL_FONT_SIZE,
            labelX,
            y,
            label
        );

        writeTextRightAligned(
            state.content,
            state.fonts.regular(),
            SMALL_FONT_SIZE,
            RIGHT_X,
            y,
            value
        );

        return y - 14.0f;
    }

    private void writeOptionalLine(
        PageState state,
        String value
    ) throws IOException {

        String normalized =
            normalize(value);

        if (normalized == null) {
            return;
        }

        writeText(
            state.content,
            state.fonts.regular(),
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

        String text = sanitize(value);

        if (text.isBlank()) {
            return;
        }

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(text);
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

        String text = sanitize(value);

        if (text.isBlank()) {
            return;
        }

        float width =
            textWidth(
                font,
                fontSize,
                text
            );

        writeText(
            content,
            font,
            fontSize,
            rightX - width,
            y,
            text
        );
    }

    private float textWidth(
        PDFont font,
        float fontSize,
        String value
    ) throws IOException {

        return font.getStringWidth(
            sanitize(value)
        ) / 1000.0f * fontSize;
    }

    private void drawLine(
        PageState state,
        float y,
        float lineWidth,
        float gray
    ) throws IOException {

        state.content.setStrokingColor(
            gray,
            gray,
            gray
        );

        state.content.setLineWidth(lineWidth);

        state.content.moveTo(
            LEFT_MARGIN,
            y
        );

        state.content.lineTo(
            LEFT_MARGIN + CONTENT_WIDTH,
            y
        );

        state.content.stroke();
    }

    private List<String> wrapText(
        String value,
        PDFont font,
        float fontSize,
        float maxWidth
    ) throws IOException {

        String normalized =
            normalize(value);

        if (normalized == null) {
            return Collections.singletonList("");
        }

        List<String> result =
            new ArrayList<>();

        for (
            String paragraph
            : normalized.split("\\R")
        ) {
            wrapParagraph(
                paragraph,
                font,
                fontSize,
                maxWidth,
                result
            );
        }

        return result.isEmpty()
            ? Collections.singletonList("")
            : result;
    }

    private void wrapParagraph(
        String paragraph,
        PDFont font,
        float fontSize,
        float maxWidth,
        List<String> result
    ) throws IOException {

        String trimmed =
            paragraph.trim();

        if (trimmed.isEmpty()) {
            result.add("");
            return;
        }

        StringBuilder line =
            new StringBuilder();

        for (
            String word
            : trimmed.split("\\s+")
        ) {
            String candidate =
                line.isEmpty()
                    ? word
                    : line + " " + word;

            if (
                textWidth(
                    font,
                    fontSize,
                    candidate
                ) <= maxWidth
            ) {
                line.setLength(0);
                line.append(candidate);
                continue;
            }

            if (!line.isEmpty()) {
                result.add(line.toString());
                line.setLength(0);
            }

            if (
                textWidth(
                    font,
                    fontSize,
                    word
                ) <= maxWidth
            ) {
                line.append(word);
            } else {
                splitLongWord(
                    word,
                    font,
                    fontSize,
                    maxWidth,
                    result,
                    line
                );
            }
        }

        if (!line.isEmpty()) {
            result.add(line.toString());
        }
    }

    private void splitLongWord(
        String word,
        PDFont font,
        float fontSize,
        float maxWidth,
        List<String> result,
        StringBuilder remainder
    ) throws IOException {

        StringBuilder part =
            new StringBuilder();

        for (char character : word.toCharArray()) {
            String candidate =
                part.toString() + character;

            if (
                part.isEmpty()
                    || textWidth(
                    font,
                    fontSize,
                    candidate
                ) <= maxWidth
            ) {
                part.append(character);
                continue;
            }

            result.add(part.toString());

            part.setLength(0);
            part.append(character);
        }

        remainder.append(part);
    }

    private String buildDescription(
        DeliveryNoteItemEntity item
    ) {
        List<String> heading =
            new ArrayList<>();

        addText(
            heading,
            item.getProductNumber()
        );

        addText(
            heading,
            item.getName()
        );

        List<String> lines =
            new ArrayList<>();

        if (!heading.isEmpty()) {
            lines.add(
                String.join(
                    " - ",
                    heading
                )
            );
        }

        addText(
            lines,
            item.getDescription()
        );

        addText(
            lines,
            item.getNotes()
        );

        return String.join(
            "\n",
            lines
        );
    }

    private void addText(
        List<String> target,
        String value
    ) {
        String normalized = normalize(value);

        if (normalized != null) {
            target.add(normalized);
        }
    }

    private CompanyEntity resolveCompany(
        DeliveryNoteEntity deliveryNote
    ) {
        CustomerEntity customer =
            deliveryNote.getCustomer();

        return customer == null
            ? null
            : customer.getCompany();
    }

    private String customerNumber(
        DeliveryNoteEntity deliveryNote
    ) {
        CustomerEntity customer =
            deliveryNote.getCustomer();

        return customer == null
            ? ""
            : safe(
            customer.getCustomerNumber()
        );
    }

    private String deliveryNoteNumber(
        DeliveryNoteEntity deliveryNote
    ) {
        String number =
            normalize(
                deliveryNote.getDeliveryNoteNumber()
            );

        return number == null
            ? "ENTWURF"
            : number;
    }

    private String formatDate(
        LocalDate date
    ) {
        return date == null
            ? ""
            : DATE_FORMAT.format(date);
    }

    private String formatQuantity(
        BigDecimal value
    ) {
        return value == null
            ? ""
            : value
            .stripTrailingZeros()
            .toPlainString()
            .replace('.', ',');
    }

    private String joinNonBlank(
        String first,
        String second
    ) {
        String firstValue = normalize(first);
        String secondValue = normalize(second);

        if (firstValue == null) {
            return safe(secondValue);
        }

        if (secondValue == null) {
            return firstValue;
        }

        return firstValue
            + " "
            + secondValue;
    }

    private String normalize(
        String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
            value.trim();

        return normalized.isEmpty()
            ? null
            : normalized;
    }

    private String safe(
        String value
    ) {
        String normalized = normalize(value);

        return normalized == null
            ? ""
            : normalized;
    }

    private String sanitize(
        String value
    ) {
        return safe(value)
            .replace("\u00A0", " ")
            .replace("€", "EUR")
            .replace("–", "-")
            .replace("—", "-")
            .replace("„", "\"")
            .replace("“", "\"")
            .replace("”", "\"")
            .replace("’", "'")
            .replace("…", "...");
    }

    private void validateDeliveryNote(
        DeliveryNoteEntity deliveryNote
    ) {
        if (deliveryNote == null) {
            throw new IllegalArgumentException(
                "Der Lieferschein darf nicht null sein."
            );
        }

        if (deliveryNote.getDeliveryDate() == null) {
            throw new IllegalArgumentException(
                "Das Lieferdatum darf nicht null sein."
            );
        }

        if (
            normalize(
                deliveryNote.getDeliveryName()
            ) == null
        ) {
            throw new IllegalArgumentException(
                "Der Lieferempfänger darf nicht leer sein."
            );
        }
    }

    private record PdfFonts(
        PDFont regular,
        PDFont bold
    ) {
    }

    private record LabelValue(
        String label,
        String value
    ) {
    }

    private record ImageSize(
        float width,
        float height
    ) {
    }

    private static final class PageState {

        private final PDPage page;
        private final PDPageContentStream content;
        private final PdfFonts fonts;
        private float y;

        private PageState(
            PDPage page,
            PDPageContentStream content,
            PdfFonts fonts,
            float y
        ) {
            this.page = page;
            this.content = content;
            this.fonts = fonts;
            this.y = y;
        }

        private void moveDown(
            float distance
        ) {
            y -= distance;
        }

        private void close()
            throws IOException {

            content.close();
        }
    }
}
