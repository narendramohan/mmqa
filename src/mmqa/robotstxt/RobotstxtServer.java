package mmqa.robotstxt;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mmqa.crawler.Page;
import mmqa.fetcher.PageFetchResult;
import mmqa.fetcher.PageFetcher;
import mmqa.url.WebURL;
import mmqa.util.Util;

import org.apache.http.HttpStatus;


public class RobotstxtServer {

	protected RobotstxtConfig config;

	protected final Map<String, HostDirectives> host2directivesCache = new HashMap<>();

	protected PageFetcher pageFetcher;

	public RobotstxtServer(RobotstxtConfig config, PageFetcher pageFetcher) {
		this.config = config;
		this.pageFetcher = pageFetcher;
	}

	private static String getHost(URL url) {
		return url.getHost().toLowerCase();
	}

	public boolean allows(WebURL webURL) {
		if (!config.isEnabled()) {
			return true;
		}
		try {
			URL url = new URL(webURL.getURL());
			String host = getHost(url);
			String path = url.getPath();

			HostDirectives directives = host2directivesCache.get(host);

			if (directives != null && directives.needsRefetch()) {
				synchronized (host2directivesCache) {
					host2directivesCache.remove(host);
					directives = null;
				}
			}

			if (directives == null) {
				directives = fetchDirectives(url);
			}
			return directives.allows(path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return true;
	}

	private HostDirectives fetchDirectives(URL url) {
		WebURL robotsTxtUrl = new WebURL();
		String host = getHost(url);
		String port = (url.getPort() == url.getDefaultPort() || url.getPort() == -1) ? "" : ":" + url.getPort();
		robotsTxtUrl.setURL("http://" + host + port + "/robots.txt");
		HostDirectives directives = null;
		PageFetchResult fetchResult = null;
		try {
			fetchResult = pageFetcher.fetchHeader(robotsTxtUrl);
			if (fetchResult.getStatusCode() == HttpStatus.SC_OK) {
				Page page = new Page(robotsTxtUrl);
				fetchResult.fetchContent(page);
				if (Util.hasPlainTextContent(page.getContentType())) {
					try {
						String content;
						if (page.getContentCharset() == null) {
							content = new String(page.getContentData());
						} else {
							content = new String(page.getContentData(), page.getContentCharset());
						}
						directives = RobotstxtParser.parse(content, config.getUserAgentName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			if (fetchResult != null) {
				fetchResult.discardContentIfNotConsumed();
			}
		}
		if (directives == null) {
			// We still need to have this object to keep track of the time we
			// fetched it
			directives = new HostDirectives();
		}
		synchronized (host2directivesCache) {
			if (host2directivesCache.size() == config.getCacheSize()) {
				String minHost = null;
				long minAccessTime = Long.MAX_VALUE;
				for (Entry<String, HostDirectives> entry : host2directivesCache.entrySet()) {
					if (entry.getValue().getLastAccessTime() < minAccessTime) {
						minAccessTime = entry.getValue().getLastAccessTime();
						minHost = entry.getKey();
					}
				}
				host2directivesCache.remove(minHost);
			}
			host2directivesCache.put(host, directives);
		}
		return directives;
	}
}
