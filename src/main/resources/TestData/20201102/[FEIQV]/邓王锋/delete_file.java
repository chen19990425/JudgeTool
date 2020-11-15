package y10_29作业;

import java.io.File;

public class delete_file {
	public static void main(String[] args) {
		File file = new File("E:\\a");
		digui(file);
	}
	public static void digui(File file) {
		if (!file.isDirectory()) {
			file.delete();
		} else {
			File files[] = file.listFiles();
			for (File item : files) {
				digui(item);
			}
			file.delete();
		}
	}
}
