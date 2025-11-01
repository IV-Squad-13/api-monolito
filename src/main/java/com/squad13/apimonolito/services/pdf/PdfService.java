package com.squad13.apimonolito.services.pdf;

import com.squad13.apimonolito.models.editor.mongo.EspecificacaoDoc;
import com.squad13.apimonolito.mongo.editor.EspecificacaoDocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final EspecificacaoDocRepository especificacaoDocRepository;

    public byte[] generateInvoiceReport() throws DocumentException {
        List<EspecificacaoDoc> invoices = especificacaoDocRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        document.add(new Paragraph("Invoice Report", fontTitle));
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(4); // 4 columns
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 3.5f, 2.0f, 2.0f});

        addTableHeader(table);

        for (EspecificacaoDoc invoice : invoices) {
            addTableRows(table, invoice);
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        String[] headers = {"ID", "Customer", "Product", "Amount"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void addTableRows(PdfPTable table, EspecificacaoDoc invoice) {
        table.addCell(invoice.getId().toHexString());
        table.addCell(invoice.getName());
        table.addCell(invoice.getObs());
        table.addCell(String.valueOf(invoice.getDesc()));
    }
}