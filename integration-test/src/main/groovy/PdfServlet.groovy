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

import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.shamdata.Sham
import org.shamdata.person.Person

import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class PdfServlet extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		byte[] data = generate(req)
		if(req.getParameter("saveInSession")) {
			req.getSession(true).setAttribute("pdf", data)
			resp.sendRedirect(req.getContextPath() + '/')
		} else {
			resp.contentType = "application/pdf"
			resp.contentLength = data.length
			OutputStream os = resp.outputStream
			os.write(data)
			os.close()
		}
	}

	private byte[] generate(HttpServletRequest req) {
		def paragraphs = req.getParameterValues("paragraphs")
		if(!paragraphs) {
			paragraphs = (1..3).collect { Sham.instance.nextSentence(60) }
		}
		Person person = Sham.instance.nextPerson()
		Document document = new Document()
		def os = new ByteArrayOutputStream()
		PdfWriter.getInstance(document, os)
		document.open()
		document.add(new Paragraph())
		PdfPTable table = new PdfPTable(2)
		PdfPCell headerCell = new PdfPCell(new Phrase("Personal Details"));
		headerCell.setColspan(2)
		table.addCell(headerCell)
		table.addCell("Name:")
		table.addCell(req.getParameter('name') ?: person.name)
		table.addCell("Email:")
		table.addCell(req.getParameter('email') ?: person.email)
		document.add(table)
		document.add(new Paragraph("Favourite prose:"))
		for(String para : paragraphs) {
			document.add(new Paragraph(para))
		}
		document.close()
		os.toByteArray()
	}
}
