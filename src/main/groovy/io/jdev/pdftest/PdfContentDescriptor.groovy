package io.jdev.pdftest

class PdfContentDescriptor {
    String name
    boolean required = true
    Class<? extends Closure> contentClosure
}
