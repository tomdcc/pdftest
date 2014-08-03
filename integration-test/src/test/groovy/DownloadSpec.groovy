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


import geb.Browser
import io.jdev.pdftest.PdfBrowser
import org.shamdata.Sham
import org.shamdata.person.Person
import spock.lang.Specification

class DownloadSpec extends Specification {

	PdfBrowser pdfBrowser
	Browser browser
	Person person
	List<String> paragraphs

	void setup() {
		browser = new Browser()
		pdfBrowser = new PdfBrowser()
		person = Sham.instance.nextPerson()
		paragraphs = paragraphs = (1..3).collect { Sham.instance.nextSentence(60) }
	}

	void "can download pdf from url"() {
		when: 'ask for pdf'
		String url = browser.calculateUri('pdf', [name: person.name, email: person.email, paragraphs: paragraphs])
		pdfBrowser.fetchDocumentFromUrl(url)

		then:
		pdfBrowser.documentIsA(PersonDocument)
		pdfBrowser.document.name == person.name
		pdfBrowser.document.email == person.email
		pdfBrowser.document.prose == paragraphs
	}

	void "can download pdf from url in session passing cookies in"() {
		when: 'go to home page'
		browser.to HomePage

		and: 'generate pdf in session'
		browser.page.saveInSessionLink.click()

		then: 'back at home page'
		browser.at HomePage

		when: 'ask for pdf'
		def sessionCookies = browser.driver.manage().cookies.collectEntries { [it.name, it.value] }
		String url = browser.calculateUri(browser.page.downloadFromSessionLink.@href, [:])
		pdfBrowser.fetchDocumentFromUrl(url, sessionCookies)

		then:
		pdfBrowser.documentIsA(PersonDocument)
		assert pdfBrowser.document.name ==~ ~/\w+ \w+/
		assert pdfBrowser.document.email ==~ ~/[\w\.]+@[\w\.]+/
		pdfBrowser.document.prose.size() == 3
	}

	void "can download pdf from url in session passing web driver in"() {
		when: 'go to home page'
		browser.to HomePage

		and: 'generate pdf in session'
		browser.page.saveInSessionLink.click()

		then: 'back at home page'
		browser.at HomePage

		when: 'ask for pdf'
		pdfBrowser.fetchDocumentFromUrl(browser.page.downloadFromSessionLink.@href, browser.driver)

		then:
		pdfBrowser.documentIsA(PersonDocument)
		assert pdfBrowser.document.name ==~ ~/\w+ \w+/
		assert pdfBrowser.document.email ==~ ~/[\w\.]+@[\w\.]+/
		pdfBrowser.document.prose.size() == 3
	}
}
