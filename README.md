pdftest [![Build Status](https://travis-ci.org/tomdcc/pdftest.svg?branch=master)](https://travis-ci.org/tomdcc/pdftest)
=======

A groovy library to test PDFs in the same way that [Geb](http://www.gebish.org/)
tests web pages.

```groovy
class PersonDocument extends PdfDocument {
    static isA = { List<String> firstPageLines -> firstPageLines.contains('Personal Details') }
    static content = {
        name  { findLine(~/Name: (.*)/).trim() }
        email { findLine(~/Email: (.*)/).trim() }
        bio { findBetweenLines(~/Bio:/, ~/Education:/) }
    }
}

// in test
PdfBrowser pdfBrowser = new PdfBrowser()
pdfBrowser.fetchDocument('http://localhost:8080/the-pdf')
assert pdfBrowser.documentIsA(PersonDocument)
assert pdfBrowser.document.name == 'My Name' 
assert pdfBrowser.document.email == 'my.email@foo.com' 
assert pdfBrowser.document.bio == ['Bio first line', 'Bio second line'] 
```

How it Works
------------
pdftest provides a [page object model](http://martinfowler.com/bliki/PageObject.html)
for PDF documents created by your application, in the same way that Geb
provides a page object model for your web pages. The API for describing your
documents is heavily inspired by Geb.

The "page object" in pdftest is a document. Documents must extends the
`PdfDocument` class. pdftest converts the pdf into text using
[iText](http://itextpdf.com/). The pdf is presented to the document class as a
list of pages, with each page being a list of strings representing a line in
the pdf. This isn't perfect, but it can handle a surprisingly large number of
cases.


PdfBrowser API
--------------
pdftest provides a `PdfBrowser` object that can either be handed the PDF data
or can download a PDF from a specific URL. You can also pass in a set of
cookies if the PDF will only be available to a current logged-in session,
or you can pass a `WebDriver` object and the `PdfBrowser` will scrape all the
cookies fmor that and add them to its request.

Having fetched the PDF, you can then call the `documentIsA` method to specify
which type of document you were expecting. This will call the `isA` static
method on the document class, passing through the first page of text.

If the documentIsA method returns successfully, the `document` property will
be populated on the `PdfBrowser` object, and you can then make assertions
about content in the document after that.

Content Utility Methods
-----------------------
Often content that you would like to examine in the converted-to-text PDF
falls into two categories:
 - Lines with a heading and some content, e.g. `Account number: 1234`
 - Lines between some other headings

pdftest makes some utility methods availabke to extract content in that format:

 - the `findLine` method takes a regular expression. If there are no capturing
   groups in the regex, it will return the whole line. If a single capturing
   group, it will return the text of that group as a string (seen in the
   example above). If multiple capturing groups, it will return a list of the
   text captured in the groups)

 - the `findBetweenLines` method, which takes optional before and after
   regex patterns and returns the lines between them
 
Reporting failure
-----------------
The `PdfDocument` class has a couple of properties useful for reporting errors:

 - the `bytes` property contains the PDF file data for the document
 - the `filename` property contains the filename of the PDF. This can come from
   the file name if the PDF was read from a file or the `Content-Disposition` header
   or URI of the PDF if it was downloaded.

Together these let you save the PDF somewhere for later inspection if your test
fails.

Installation
------------

pdftest is deployed to Maven Central and can be added to your project as a dependency using the following coordinates:

    groupId: io.jdev.pdf
    artifactId: pdftest
    version: 0.2

Or just download the jar from http://search.maven.org/ if your build system is a bit less connected.

Changelog
---------

#### Version 0.2
 - Expose `bytes` and `filename` properties for later inspection of failing PDFs

#### Version 0.1
 - Initial version
 - Basic content
