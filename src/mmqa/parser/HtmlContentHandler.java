package mmqa.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class HtmlContentHandler extends DefaultHandler {

	private final int MAX_ANCHOR_LENGTH = 100;

	private enum Element {
		A, AREA, LINK, IFRAME, FRAME, EMBED, IMG, BASE, META, BODY
	}

	private static class HtmlFactory {
		private static Map<String, Element> name2Element;

		static {
			name2Element = new HashMap<>();
			for (Element element : Element.values()) {
				name2Element.put(element.toString().toLowerCase(), element);
			}
		}

		public static Element getElement(String name) {
			return name2Element.get(name);
		}
	}

	private String base;
	private String metaRefresh;
	private String metaLocation;

	private boolean isWithinBodyElement;
	private StringBuilder bodyText;

	private List<ExtractedUrlAnchorPair> outgoingUrls;

	private ExtractedUrlAnchorPair curUrl = null;
	private boolean anchorFlag = false;
	private StringBuilder anchorText = new StringBuilder();

	public HtmlContentHandler() {
		isWithinBodyElement = false;
		bodyText = new StringBuilder();
		outgoingUrls = new ArrayList<>();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		Element element = HtmlFactory.getElement(localName);

		if (element == Element.A || element == Element.AREA || element == Element.LINK) {
			String href = attributes.getValue("href");
			if (href != null) {
				anchorFlag = true;
				curUrl = new ExtractedUrlAnchorPair();
				curUrl.setHref(href);
				outgoingUrls.add(curUrl);
			}
			return;
		}

		if (element == Element.IMG) {
			String imgSrc = attributes.getValue("src");
			if (imgSrc != null) {
				curUrl = new ExtractedUrlAnchorPair();
				curUrl.setHref(imgSrc);
				outgoingUrls.add(curUrl);
			}
			return;
		}

		if (element == Element.IFRAME || element == Element.FRAME || element == Element.EMBED) {
			String src = attributes.getValue("src");
			if (src != null) {
				curUrl = new ExtractedUrlAnchorPair();
				curUrl.setHref(src);
				outgoingUrls.add(curUrl);
			}
			return;
		}

		if (element == Element.BASE) {
			if (base != null) { // We only consider the first occurrence of the
								// Base element.
				String href = attributes.getValue("href");
				if (href != null) {
					base = href;
				}
			}
			return;
		}

		if (element == Element.META) {
			String equiv = attributes.getValue("http-equiv");
			String content = attributes.getValue("content");
			if (equiv != null && content != null) {
				equiv = equiv.toLowerCase();

				// http-equiv="refresh" content="0;URL=http://foo.bar/..."
				if (equiv.equals("refresh") && (metaRefresh == null)) {
					int pos = content.toLowerCase().indexOf("url=");
					if (pos != -1) {
						metaRefresh = content.substring(pos + 4);
					}
					curUrl = new ExtractedUrlAnchorPair();
					curUrl.setHref(metaRefresh);
					outgoingUrls.add(curUrl);
				}

				// http-equiv="location" content="http://foo.bar/..."
				if (equiv.equals("location") && (metaLocation == null)) {
					metaLocation = content;
					curUrl = new ExtractedUrlAnchorPair();
					curUrl.setHref(metaRefresh);
					outgoingUrls.add(curUrl);
				}
			}
			return;
		}

		if (element == Element.BODY) {
			isWithinBodyElement = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		Element element = HtmlFactory.getElement(localName);
		if (element == Element.A || element == Element.AREA || element == Element.LINK) {
			anchorFlag = false;
			if (curUrl != null) {
				String anchor = anchorText.toString().replaceAll("\n", " ").replaceAll("\t", " ").trim();
				if (!anchor.isEmpty()) {
					if (anchor.length() > MAX_ANCHOR_LENGTH) {
						anchor = anchor.substring(0, MAX_ANCHOR_LENGTH) + "...";
					}
					curUrl.setAnchor(anchor);
				}
				anchorText.delete(0, anchorText.length());
			}
			curUrl = null;
		}
		if (element == Element.BODY) {
			isWithinBodyElement = false;
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (isWithinBodyElement) {
			bodyText.append(ch, start, length);

			if (anchorFlag) {
				anchorText.append(new String(ch, start, length));
			}
		}
	}

	public String getBodyText() {
		return bodyText.toString();
	}

	public List<ExtractedUrlAnchorPair> getOutgoingUrls() {
		return outgoingUrls;
	}

	public String getBaseUrl() {
		return base;
	}

}
