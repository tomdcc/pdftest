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

import java.util.regex.Matcher
import java.util.regex.Pattern

class PdfParser {

    public PdfData parsePdf(String url, Map<String,String> cookies = [:]) {
        PdfData data = downloadUrl(url, cookies)
        data.pages = pdfToText(data.bytes)
        data
    }

    public PdfData parsePdf(byte[] data, String filename = null) {
        new PdfData(bytes: data, filename: filename, pages: pdfToText(data))
    }

    public PdfData parsePdf(File file) {
        byte[] data = file.bytes
        parsePdf(data, file.name)
    }

    public List<List<String>> pdfToText(byte[] data) {
        pdfToText(new PdfReader(data))
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

    PdfData downloadUrl(String urlStr, Map<String,String> cookies) {
        URL url = new URL(urlStr)
        HttpURLConnection con = url.openConnection() as HttpURLConnection
        PdfData data = downloadUrl(con, cookies)
        if(!data.filename) {
            // no attachment filename specified, use url
            data.filename = urlToFilename(urlStr)
        }
        data
    }

    private String urlToFilename(String urlStr) {
        // string off query string
        int qsPlace = urlStr.indexOf('?')
        String filename = qsPlace == -1 ? urlStr : urlStr.substring(0, qsPlace)

        // strip up to last '/'
        filename = filename.substring(filename.lastIndexOf('/') + 1)

        // if nothing else left,  (e.g. was http://foo.com/' with no filename)
        // then use modified hostname instead
        if(!filename) {
            filename = new URL(urlStr).host.replace('.' as char, '-' as char)
        }

        // add .pdf on the end if it doesn't have it, for easy saving and
        // subsequent opening
        if(!filename.toLowerCase().endsWith('.pdf')) {
            filename += '.pdf'
        }
        println filename
        filename
    }

    PdfData downloadUrl(HttpURLConnection con, Map<String,String> cookies) {
        if(cookies) {
            cookies.each { key, value ->
                con.setRequestProperty("Cookie", "$key=$value")
            }
        }
        PdfData data = new PdfData()
        data.bytes = con.inputStream.bytes
        data.filename = extractFilenameFromHeader(con)
        data
    }

    private static final Pattern CONTENT_DISP_ATTACHMENT_PATTERN = ~/(?i)attachment; filename=(.*)/
    private String extractFilenameFromHeader(HttpURLConnection con) {
        String contentDisp = con.getHeaderField("Content-Disposition")
        println "contentDisp: [$contentDisp]"
        if(contentDisp) {
            Matcher m = CONTENT_DISP_ATTACHMENT_PATTERN.matcher(contentDisp)
            if(m.matches()) {
                return m.group(1)
            }
        }
        null
    }

}
