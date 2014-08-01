package io.jdev.pdftest

class PdfContentMissingException extends PdfTestException {
    public PdfContentMissingException() {
    }
    public PdfContentMissingException(PdfDocument document, Object name, Throwable cause) {
        super(String.format("%s content with name '%s' not found", document, name), cause);
    }
}
