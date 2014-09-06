package mmqa.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IO {

	public static boolean deleteFolder(File folder) {
		return deleteFolderContents(folder) && folder.delete();
	}
	
	public static boolean deleteFolderContents(File folder) {
		System.out.println("Deleting content of: " + folder.getAbsolutePath());
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				if (!file.delete()) {
					return false;
				}
			} else {
				if (!deleteFolder(file)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void writeBytesToFile(byte[] bytes, String destination) {
		FileChannel fc=null;
		try {
			fc = new FileOutputStream(destination).getChannel();
			fc.write(ByteBuffer.wrap(bytes));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fc!=null )
				try {
					fc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
