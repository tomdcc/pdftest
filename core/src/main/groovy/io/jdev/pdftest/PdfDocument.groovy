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

package io.jdev.pdftest;

import java.util.regex.Pattern;

public abstract class PdfDocument {
	List<List<String>> pages
	List<String> cachedLines
	
	public List<String> getAllLines() {
		if(!cachedLines) {
			cachedLines = pages.flatten()
		}
		cachedLines
	}

    // cache these across the vm lifetime - should move this to a dedicated cache somewhere
	private static Map<Class<? extends PdfDocument>,Map<String,PdfContentDescriptor>> contentClosures = [:]

    // cache of content per document
	private Map cachedContent = [:]

	def propertyMissing(String propName) {
		def value = cachedContent[propName]
		if(!value) {
            Class<? extends PdfDocument> thisClass = getClass() as Class<? extends PdfDocument>
            PdfContentDescriptor contentDescriptor = getContentClosures(thisClass)[propName]
			if(contentDescriptor == null) {
				throw new PdfContentUndefinedException(this, propName)
			}
			def valueClosure = contentDescriptor.contentClosure.newInstance(this, this)
            try {
                value = valueClosure()
            } catch(Exception e) {
                if(contentDescriptor.required) {
                    throw new PdfContentMissingException(this, propName, e)
                }
            }
			cachedContent[propName] = value
		}
		value
	}

	private static Map<String,PdfContentDescriptor> getContentClosures(Class<? extends PdfDocument> cls) {
        Map<String,PdfContentDescriptor> clsClosures = contentClosures[cls]
		if(!clsClosures) {
            PdfContentDelegate delegate = new PdfContentDelegate()
			Closure contentClosure = cls.content
			contentClosure.delegate = delegate
			contentClosure.resolveStrategy = Closure.DELEGATE_FIRST
			contentClosure()
			clsClosures = delegate.contentClosures
			contentClosures[cls] = clsClosures
		}
		clsClosures
	}

	protected findLine(Pattern pattern) {
		findLineOnPage(pattern, null)
	}

	protected def findLineOnPage(Pattern pattern, Integer pageIndex) {
		def lines = pageIndex == null ? allLines : pages[pageIndex]
		for(String line : lines) {
			def matcher = pattern.matcher(line)
			if(matcher.matches()) {
				def groups = matcher.groupCount()
				switch(groups){
					case 0:
					// no capturing groups, return the whole line
					return line
					
					case 1:
					// simple case returned directly so we don't have to append [0] on the end all the time
					return matcher.group(1)
					
					default:
					// return all capturing groups to caller
					return (1..groups).collect { matcher.group(it) }
				}
			}
		}
        throw new PdfContentMissingException()
	}

	protected List<String> findBetweenLines(Pattern beforePattern, Pattern afterPattern) {
		findBetweenLinesOnPage(beforePattern, afterPattern, null)
	}
	
	protected List<String> findBetweenLinesOnPage(Pattern beforePattern, Pattern afterPattern, Integer pageIndex) {
		def lines = pageIndex == null ? allLines : pages[pageIndex]
		int start
		if(beforePattern) {
			start = -1
			for(int i = 0; i < lines.size(); i++) {
				String line = lines[i]
				if(beforePattern.matcher(line).matches()) {
					// start on the line after the match
					start = i + 1
					break
				}
			}
			if(start == -1) {
                throw new PdfContentMissingException()
            }
		} else {
			// just start at the first line
			start = 0
		}

		int end = -1
		List<String> content = []
		for(int i = start; i < lines.size(); i++) {
			String line = lines[i]
			if(afterPattern) {
				if(afterPattern.matcher(line).matches()) {
					end = i
					break
				}
			}
			content.add(line)
		}
		if(afterPattern && end == -1) {
            throw new PdfContentMissingException()
        }

		content
	}

	String dumpText() {
		pages.collect{ it.join("\n") }.join("\n=======================================\n")
	}

    String toString() {
        getClass().getSimpleName()
    }
}
