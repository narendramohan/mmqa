package mmqa.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList; 
import java.util.List;

import mmqa.crawler.Configurable;
import mmqa.crawler.CrawlConfig;
import mmqa.crawler.Page;
import mmqa.url.URLCanonicalizer;
import mmqa.url.WebURL;
import mmqa.util.Util;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;



public class Parser extends Configurable {

	protected static final Logger logger = Logger.getLogger(Parser.class.getName());

	private HtmlParser htmlParser;
	private ParseContext parseContext;

	public Parser(CrawlConfig config) {
		super(config);
		htmlParser = new HtmlParser();
		parseContext = new ParseContext();
	}

	public boolean parse(Page page, String contextURL) {

		if (Util.hasBinaryContent(page.getContentType())) {
			if (!config.isIncludeBinaryContentInCrawling()) {
				return false;
			}

			page.setParseData(BinaryParseData.getInstance());
			return true;

		} else if (Util.hasPlainTextContent(page.getContentType())) {
			try {
				TextParseData parseData = new TextParseData();
				if (page.getContentCharset() == null) {
					parseData.setTextContent(new String(page.getContentData()));
				} else {
					parseData.setTextContent(new String(page.getContentData(), page.getContentCharset()));
				}
				page.setParseData(parseData);
				return true;
			} catch (Exception e) {
				logger.error(e.getMessage() + ", while parsing: " + page.getWebURL().getURL());
			}
			return false;
		}

		Metadata metadata = new Metadata();
		HtmlContentHandler contentHandler = new HtmlContentHandler();
		InputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(page.getContentData());
			htmlParser.parse(inputStream, contentHandler, metadata, parseContext);
		} catch (Exception e) {
			logger.error(e.getMessage() + ", while parsing: " + page.getWebURL().getURL());
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage() + ", while parsing: " + page.getWebURL().getURL());
			}
		}

		if (page.getContentCharset() == null) {
			page.setContentCharset(metadata.get("Content-Encoding"));
		}

		HtmlParseData parseData = new HtmlParseData();
		parseData.setText(contentHandler.getBodyText().trim());
		parseData.setTitle(metadata.get(DublinCore.TITLE));

		List<WebURL> outgoingUrls = new ArrayList<>();

		String baseURL = contentHandler.getBaseUrl();
		if (baseURL != null) {
			contextURL = baseURL;
		}

		int urlCount = 0;
		for (ExtractedUrlAnchorPair urlAnchorPair : contentHandler.getOutgoingUrls()) {
			String href = urlAnchorPair.getHref();
			href = href.trim();
			if (href.length() == 0) {
				continue;
			}
			String hrefWithoutProtocol = href.toLowerCase();
			if (href.startsWith("http://")) {
				hrefWithoutProtocol = href.substring(7);
			}
			if (!hrefWithoutProtocol.contains("javascript:") && !hrefWithoutProtocol.contains("mailto:")
					&& !hrefWithoutProtocol.contains("@")) {
				String url = URLCanonicalizer.getCanonicalURL(href, contextURL);
				if (url != null) {
					WebURL webURL = new WebURL();
					webURL.setURL(url);
					webURL.setAnchor(urlAnchorPair.getAnchor());
					outgoingUrls.add(webURL);
					urlCount++;
					if (urlCount > config.getMaxOutgoingLinksToFollow()) {
						break;
					}
				}
			}
		}

		parseData.setOutgoingUrls(outgoingUrls);

		try {
			if (page.getContentCharset() == null) {
				parseData.setHtml(new String(page.getContentData()));
			} else {
				parseData.setHtml(new String(page.getContentData(), page.getContentCharset()));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}

		page.setParseData(parseData);
		return true;

	}

}
