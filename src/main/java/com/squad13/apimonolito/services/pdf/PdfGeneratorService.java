package com.squad13.apimonolito.services.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGeneratorService {

    public byte[] gerarPdfSimples(String titulo, String conteudo) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();
            document.add(new Paragraph(titulo));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(conteudo));
            document.close();

            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}