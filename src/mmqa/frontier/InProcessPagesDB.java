package mmqa.frontier;

import mmqa.url.WebURL;

import org.apache.log4j.Logger;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

/**
 * This class maintains the list of pages which are
 * assigned to crawlers but are not yet processed.
 * It is used for resuming a previous crawl. 
 * 
 */
public class InProcessPagesDB extends WorkQueues {

	private static final Logger logger = Logger.getLogger(InProcessPagesDB.class.getName());
		
	public InProcessPagesDB(Environment env) throws DatabaseException {
		super(env, "InProcessPagesDB", true);
		long docCount = getLength();
		if (docCount > 0) {
			logger.info("Loaded " + docCount + " URLs that have been in process in the previous crawl.");
		}
	}

	public boolean removeURL(WebURL webUrl) {
		synchronized (mutex) {
			try {
				DatabaseEntry key = getDatabaseEntryKey(webUrl);				
				Cursor cursor = null;
				OperationStatus result;
				DatabaseEntry value = new DatabaseEntry();
				Transaction txn = env.beginTransaction(null, null);
				try {
					cursor = urlsDB.openCursor(txn, null);
					result = cursor.getSearchKey(key, value, null);
					
					if (result == OperationStatus.SUCCESS) {
						result = cursor.delete();
						if (result == OperationStatus.SUCCESS) {
							return true;
						}
					}
				} catch (DatabaseException e) {
					if (txn != null) {
						txn.abort();
						txn = null;
					}
					throw e;
				} finally {
					if (cursor != null) {
						cursor.close();
					}
					if (txn != null) {
						txn.commit();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
