package mmqa.controller;

import mmqa.crawler.CrawlConfig;
import mmqa.crawler.CrawlController;
import mmqa.fetcher.PageFetcher;
import mmqa.imagecrawler.ImageCrawler;
import mmqa.robotstxt.RobotstxtConfig;
import mmqa.robotstxt.RobotstxtServer;
import mmqa.video.VideoCrawler;

public class BasicCrawlController extends Thread{
	
	public BasicCrawlController(String path, String noofthread) {
		crawlerpath = path;
		this.noofthread = noofthread; 
	}
	public void run() {
		myCrawler();
	}
	String crawlerpath="C:\\MyProject\\eclipse\\workspace\\mmqa\\data";
	String noofthread ="2";
	public void myCrawler() {
		
		//crawlerpath="C:\\MyProject\\eclipse\\workspace\\mmqa\\data";
		noofthread = "4";

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
         * Be polite: Make sure that we don't send more than 1 request per
         * second (1000 milliseconds between requests).
         */
        config.setPolitenessDelay(1000);

        /*
         * You can set the maximum crawl depth here. The default value is -1 for
         * unlimited depth
         */
        config.setMaxDepthOfCrawling(-1);

        /*
         * You can set the maximum number of pages to crawl. The default value
         * is -1 for unlimited number of pages
         */
        config.setMaxPagesToFetch(-1);

        /*
         * Do you need to set a proxy? If so, you can use:
         * config.setProxyHost("proxyserver.example.com");
         * config.setProxyPort(8080);
         * 
         * If your proxy also needs authentication:
         * config.setProxyUsername(username); config.getProxyPassword(password);
         */

        /*
         * This config parameter can be used to set your crawl to be resumable
         * (meaning that you can resume the crawl from a previously
         * interrupted/crashed crawl). Note: if you enable resuming feature and
         * want to start a fresh crawl, you need to delete the contents of
         * rootFolder manually.
         */
        config.setResumableCrawling(true);

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller=null;
		try {
			controller = new CrawlController(config, pageFetcher, robotstxtServer);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
		 String[] crawlDomains = new String[] { "http://en.wikipedia.org/" };
		 String[] videoCcrawlDomains = new String[] { "http://gdata.youtube.com/feeds/api/videos" };
		 controller.addSeed("http://wiki.answers.com/Q");
		 controller.addSeed("http://www.tutorialspoint.com/cloud_computing/cloud_computing_overview.htm");
		 controller.addSeed("http://www.tutorialspoint.com/java/java_interview_questions.htm");
         controller.addSeed("http://www.indiabix.com/technical/core-java/");
         controller.addSeed("http://www.allapplabs.com/interview_questions/java_interview_questions.htm");
         controller.addSeed("http://www.answers.com/T/Java_Programming");
         controller.addSeed("http://gdata.youtube.com/feeds/api/videos");
        //controller.addSeed("http://www.ics.uci.edu/~welling/");

        /*
         * Start the crawl. This is a blocking operation, meaning that your code
         * will reach the line after this only when crawling is finished.
         */
        controller.start(BasicCrawler.class, numberOfCrawlers);
       // ImageCrawler.configure(crawlDomains, crawlStorageFolder);
       // VideoCrawler.configure(crawlDomains, crawlStorageFolder);
		//controller.start(ImageCrawler.class, numberOfCrawlers);
        controller.start(VideoCrawler.class, numberOfCrawlers);
	}
        public static void main(String[] args) throws Exception {
               

                /*
                 * crawlStorageFolder is a folder where intermediate crawl data is
                 * stored.
                 */
                String crawlStorageFolder = "C:\\MyProject\\eclipse\\workspace\\mmqa\\data";

                /*
                 * numberOfCrawlers shows the number of concurrent threads that should
                 * be initiated for crawling.
                 */
                int numberOfCrawlers = Integer.parseInt(args[1]);

                CrawlConfig config = new CrawlConfig();

                config.setCrawlStorageFolder(crawlStorageFolder);

                /*
                 * Be polite: Make sure that we don't send more than 1 request per
                 * second (1000 milliseconds between requests).
                 */
                config.setPolitenessDelay(1000);

                /*
                 * You can set the maximum crawl depth here. The default value is -1 for
                 * unlimited depth
                 */
                config.setMaxDepthOfCrawling(2);

                /*
                 * You can set the maximum number of pages to crawl. The default value
                 * is -1 for unlimited number of pages
                 */
                config.setMaxPagesToFetch(1000);

                /*
                 * Do you need to set a proxy? If so, you can use:
                 * config.setProxyHost("proxyserver.example.com");
                 * config.setProxyPort(8080);
                 * 
                 * If your proxy also needs authentication:
                 * config.setProxyUsername(username); config.getProxyPassword(password);
                 */

                /*
                 * This config parameter can be used to set your crawl to be resumable
                 * (meaning that you can resume the crawl from a previously
                 * interrupted/crashed crawl). Note: if you enable resuming feature and
                 * want to start a fresh crawl, you need to delete the contents of
                 * rootFolder manually.
                 */
                config.setResumableCrawling(false);

                /*
                 * Instantiate the controller for this crawl.
                 */
                PageFetcher pageFetcher = new PageFetcher(config);
                RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
                RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
                CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

                /*
                 * For each crawl, you need to add some seed urls. These are the first
                 * URLs that are fetched and then the crawler starts following links
                 * which are found in these pages
                 */
                String[] crawlDomains = new String[] { "http://uci.edu/" };
                controller.addSeed("http://wiki.answers.com/");
                controller.addSeed("http://www.tutorialspoint.com/java/java_interview_questions.htm");
                controller.addSeed("http://www.indiabix.com/technical/core-java/");
                controller.addSeed("http://www.developersbook.com/");
                for (String domain : crawlDomains) {
        			controller.addSeed(domain);
        		}
                /*
                 * Start the crawl. This is a blocking operation, meaning that your code
                 * will reach the line after this only when crawling is finished.
                 */
                controller.start(BasicCrawler.class, numberOfCrawlers);
               
        }
}