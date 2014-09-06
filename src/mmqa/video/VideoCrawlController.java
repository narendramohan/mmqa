package mmqa.video;


import mmqa.crawler.CrawlConfig;
import mmqa.crawler.CrawlController;
import mmqa.fetcher.PageFetcher;
import mmqa.robotstxt.RobotstxtConfig;
import mmqa.robotstxt.RobotstxtServer;


/*
 * IMPORTANT: Make sure that you update crawler4j.properties file and set
 * crawler.include_images to true
 */

public class VideoCrawlController extends Thread{
	public VideoCrawlController(String path, String noofthread) {
		crawlerpath = path;
		this.noofthread = noofthread;
	}
	public void run() {
		myCrawler();
	}
	String crawlerpath="C:\\MyProject\\eclipse\\workspace\\mmqa\\data";
	String noofthread = "2";
	public void myCrawler() {
		
		// load properties from disk, do be used by subsequent doGet() calls
        /*
         * crawlStorageFolder is a folder where intermediate crawl data is
         * stored.
         */
        String crawlStorageFolder = crawlerpath;

        /*
         * numberOfCrawlers shows the number of concurrent threads that should
         * be initiated for crawling.
         */
        int numberOfCrawlers = Integer.parseInt(noofthread);

        CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Since images are binary content, we need to set this parameter to
		 * true to make sure they are included in the crawl.
		 */
		config.setIncludeBinaryContentInCrawling(true);

		String[] crawlDomains = new String[] { "http://uci.edu/" };

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
			for (String domain : crawlDomains) {
				controller.addSeed(domain);
			}

			VideoCrawler.configure(crawlDomains, crawlStorageFolder);

			controller.start(VideoCrawler.class, numberOfCrawlers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Needed parameters: ");
			System.out.println("\t rootFolder (it will contain intermediate crawl data)");
			System.out.println("\t numberOfCralwers (number of concurrent threads)");
			System.out.println("\t storageFolder (a folder for storing downloaded images)");
			return;
		}
		String rootFolder = args[0];
		int numberOfCrawlers = Integer.parseInt(args[1]);
		String storageFolder = args[2];

		CrawlConfig config = new CrawlConfig();

		config.setCrawlStorageFolder(rootFolder);

		/*
		 * Since images are binary content, we need to set this parameter to
		 * true to make sure they are included in the crawl.
		 */
		config.setIncludeBinaryContentInCrawling(true);

		String[] crawlDomains = new String[] { "http://uci.edu/" };

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		for (String domain : crawlDomains) {
			controller.addSeed(domain);
		}

		VideoCrawler.configure(crawlDomains, storageFolder);

		controller.start(VideoCrawler.class, numberOfCrawlers);
	}

}
