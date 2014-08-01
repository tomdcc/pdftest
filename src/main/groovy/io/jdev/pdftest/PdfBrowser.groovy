package io.jdev.pdftest

public class PdfBrowser {

    List<List<String>> pages
    PdfParser pdfParser = new PdfParser()

    PdfDocument document

    void fetchDocumentFromUrl(String url, Map<String,String> cookies = [:]) {
        document = null
        pages = pdfParser.pdfToTextFromUrl(url, cookies)
    }

    void fetchDocument(byte[] data) {
        document = null
        pages = pdfParser.pdfToText(data)
    }

    boolean documentIsA(Class<? extends PdfDocument> requiredClass) {
        def result = requiredClass.isA.call(pages[0])
        if(result) {
            document = requiredClass.newInstance()
            document.pages = pages
            true
        } else {
            throw new PdfIncorrectException(requiredClass)
        }
    }
}
