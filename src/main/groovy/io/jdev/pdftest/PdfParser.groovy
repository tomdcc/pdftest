package io.jdev.pdftest

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfReaderContentParser
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy
import com.itextpdf.text.pdf.parser.TextExtractionStrategy

class PdfParser {

    public List<List<String>> pdfToTextFromUrl(String url, Map<String,String> cookies = [:]) {
        pdfToText(downloadUrl(url, cookies))
    }

    public List<List<String>> pdfToText(byte[] data) {
        return pdfToText(new PdfReader(data))
    }

    public List<List<String>> pdfToText(PdfReader reader) {
        PdfReaderContentParser parser = new PdfReaderContentParser(reader)
        List<List<String>> pages = []
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            TextExtractionStrategy strategy = parser.processContent(i, new SimpleTextExtractionStrategy())
            String text = strategy.getResultantText()
            String[] lines = text.split(/[\r\n]+/)
            pages.add(lines as List)
        }
        reader.close()
        pages
    }

    byte[] downloadUrl(String urlStr, Map<String,String> cookies) {
        URL url = new URL(urlStr)
        HttpURLConnection con = url.openConnection() as HttpURLConnection
        if(cookies) {
            cookies.each { key, value ->
                con.setRequestProperty("Cookie", "$key=$value")
            }
        }
        con.inputStream.bytes
    }

}
