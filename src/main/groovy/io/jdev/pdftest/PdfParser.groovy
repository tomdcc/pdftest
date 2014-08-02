/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 the original author or authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
