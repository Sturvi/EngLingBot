package com.example.englingbot.service.worddocumentservices;

import org.apache.poi.util.DocumentFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class TempWordDocument {

    private final XWPFDocument document;
    private File tempFile;

    public TempWordDocument() {
        this.document = new XWPFDocument();
    }

    protected void setDocumentName(String name) {
        // Create a temporary file with a prefix and a suffix
        try {
            tempFile = File.createTempFile(name, ".docx");
        } catch (IOException e) {
            throw new WordCreatorException(e.toString());
        }

        // Ensure the file is deleted on JVM exit
        tempFile.deleteOnExit();
    }

    protected void addSectionTitle(String title) {
        // Create a new paragraph
        XWPFParagraph paragraph = document.createParagraph();

        // Set the paragraph style
        paragraph.setStyle("Heading1");

        // Create a new run (block of text)
        XWPFRun run = paragraph.createRun();
        run.setText(title);

        // Set the font size and make it bold
        run.setFontSize(14);
        run.setBold(true);
    }

    protected void addText(String text) {
        // Create a new paragraph
        XWPFParagraph paragraph = document.createParagraph();

        // Set spacing to 0 after the paragraph to avoid extra space
        paragraph.setSpacingAfter(0);

        // Set line spacing to single.
        // This can be skipped if the default line spacing is already single
        paragraph.setSpacingBetween(1);

        // Create a new run (block of text)
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }


    protected void addNewLine() {
        // Create a new paragraph to simulate a newline
        document.createParagraph();
    }

    protected void addNewLine(int count){
        for (int i = 0; i < count; i++) {
            addNewLine();
        }
    }

    protected File getFile() {
        if (tempFile == null) {
            try {
                tempFile = File.createTempFile("tempFile", ".docx");
            } catch (IOException e) {
                throw new WordCreatorException(e.toString());
            }
        }

        // Write the document to a file
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            document.write(out);
            System.out.println("Document written successfully to temporary file: " + tempFile.getAbsolutePath());
        } catch (IOException e) {
            //Добавить логирование ошибки
        }
        return tempFile;
    }
}
