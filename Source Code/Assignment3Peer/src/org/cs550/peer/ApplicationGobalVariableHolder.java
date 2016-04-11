package org.cs550.peer;

public class ApplicationGobalVariableHolder {

	private static ApplicationGobalVariableHolder applicationGobalVariableHolder = new ApplicationGobalVariableHolder();

	public ApplicationGobalVariableHolder() {
		// TODO Auto-generated constructor stub
	}

	private String appDir;
	private String[] filesToRunPerformance;
	private int relicationNo;
	private String performacedir;


	public String getPerformacedir() {
		return performacedir;
	}

	public void setPerformacedir(String performacedir) {
		this.performacedir = performacedir;
	}

	public String[] getFilesToRunPerformance() {
		return filesToRunPerformance;
	}

	public void setFilesToRunPerformance(String[] filesToRunPerformance) {
		this.filesToRunPerformance = filesToRunPerformance;
	}

	public int getRelicationNo() {
		return relicationNo;
	}

	public void setRelicationNo(int relicationNo) {
		this.relicationNo = relicationNo;
	}

	public String getAppDir() {
		return appDir;
	}

	public void setAppDir(String appDir) {
		this.appDir = appDir;
	}

	public static ApplicationGobalVariableHolder getInstance() {
		if (applicationGobalVariableHolder == null) {
			applicationGobalVariableHolder = new ApplicationGobalVariableHolder();
		}
		return applicationGobalVariableHolder;
	}

}
