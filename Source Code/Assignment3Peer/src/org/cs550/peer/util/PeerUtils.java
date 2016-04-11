package org.cs550.peer.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class PeerUtils {

	public static List<String> getFileListFromDir(String path) {
		File coorectFile = new File(path);
		List<String> clientFileList = new ArrayList<String>();
		Collection<File> files = FileUtils.listFiles(coorectFile, null, true);
		for (File file : files) {
			clientFileList.add(file.getAbsolutePath());
		}
		return clientFileList;
	}
}
