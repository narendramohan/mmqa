
package mmqa.crawler;

import java.nio.charset.Charset;

import mmqa.parser.ParseData;
import mmqa.url.WebURL;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;

/**
 * This class contains the data for a fetched and parsed page.
 *
 */
public class Page {

    /**
     * The URL of this page.
     */
    protected WebURL url;

    /**
     * The content of this page in binary format.
     */
    protected byte[] contentData;

    /**
     * The ContentType of this page.
     * For example: "text/html; charset=UTF-8"
     */
    protected String contentType;

    /**
     * The encoding of the content.
     * For example: "gzip"
     */
    protected String contentEncoding;

    /**
     * The charset of the content.
     * For example: "UTF-8"
     */
    protected String contentCharset;
    
    /**
     * Headers which were present in the response of the
     * fetch request
     */
    protected Header[] fetchResponseHeaders;

    /**
     * The parsed data populated by parsers
     */
    protected ParseData parseData;

	public Page(WebURL url) {
		this.url = url;
	}

	public WebURL getWebURL() {
		return url;
	}

	public void setWebURL(WebURL url) {
		this.url = url;
	}

    /**
     * Loads the content of this page from a fetched
     * HttpEntity.
     */
	public void load(HttpEntity entity) throws Exception {

		contentType = null;
		Header type = entity.getContentType();
		if (type != null) {
			contentType = type.getValue();
		}

		contentEncoding = null;
		Header encoding = entity.getContentEncoding();
		if (encoding != null) {
			contentEncoding = encoding.getValue();
		}

		Charset charset = ContentType.getOrDefault(entity).getCharset();
		if (charset != null) {
			contentCharset = charset.displayName();	
		}

		contentData = EntityUtils.toByteArray(entity);
	}
	
	/**
     * Returns headers which were present in the response of the
     * fetch request
     */
	public Header[] getFetchResponseHeaders() {
		return fetchResponseHeaders;
	}
	
	public void setFetchResponseHeaders(Header[] headers) {
		fetchResponseHeaders = headers;
	}

    /**
     * Returns the parsed data generated for this page by parsers
     */
	public ParseData getParseData() {
		return parseData;
	}

	public void setParseData(ParseData parseData) {
		this.parseData = parseData;
	}

    /**
     * Returns the content of this page in binary format.
     */
	public byte[] getContentData() {
		return contentData;
	}

	public void setContentData(byte[] contentData) {
		this.contentData = contentData;
	}

    /**
     * Returns the ContentType of this page.
     * For example: "text/html; charset=UTF-8"
     */
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

    /**
     * Returns the encoding of the content.
     * For example: "gzip"
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * Returns the charset of the content.
     * For example: "UTF-8"
     */
	public String getContentCharset() {
		return contentCharset;
	}

	public void setContentCharset(String contentCharset) {
		this.contentCharset = contentCharset;
	}

}
