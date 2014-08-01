package io.jdev.pdftest

class PdfContentUndefinedException extends PdfTestException {
    public PdfContentUndefinedException(PdfDocument document, Object name) {
        super(String.format("%s does not define content with name '%s'", document, name));
    }
}
