package 作业11_2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ZZMain {
	public static void main(String[] args) {
		// 修改该目录选择需要拷贝的文件夹   将拷贝到同一个文件夹下
		File file_one = new File("E:\\ceshi1");
		copy(file_one);
	}

	// 非目录的拷贝  一个文件拷贝到另一个文件里
	public static void insertData(File file1, File file2) {
		try {
			InputStream is = new FileInputStream(file1);
			OutputStream os = new FileOutputStream(file2);
			byte[] b = new byte[1024];
			int length = -1;
			while ((length = is.read(b)) > 0 ) {
				os.write(b, 0, length);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 拷贝一个文件 拷贝在同一个目录下
	public static void copy(File file_one) {
		String str = file_one.getParent() + "\\" + file_one.getName().split("\\.")[0] + "_副本";
		if (!file_one.isDirectory()) {
			String str_emp[] = file_one.getName().split("\\.");
			str += "." + str_emp[str_emp.length-1];
			File file_two = new File(str);
			insertData(file_one, file_two);
		} else {
			File file_two = new File(str);
			file_two.mkdirs();
			mulu_copy(file_one, file_two);
		}
	}

	//一个目录里的东西拷贝到另一个目录
	private static void mulu_copy(File file_one, File file_two) {
		// TODO Auto-generated method stub
		File[] list_one = file_one.listFiles();
		File[] list_two = file_two.listFiles();
		for (File item : list_one) {
			String str_one = file_two.getPath() + "\\" + item.getName();
			String[] str_emp = str_one.split("\\\\");
			String str_finally = "";
			for (int i = 0; i < str_emp.length; i++) {
				str_finally += str_emp[i];
				if (i == str_emp.length-1)
					break;
				str_finally += "\\\\";
			}
			if (item.isDirectory()) {
				File file_nextDir = new File(str_finally);
				file_nextDir.mkdirs();
				mulu_copy(item, file_nextDir);
			} else {
				File file_nextFile = new File(str_finally);
				try {
					if (file_nextFile.createNewFile()) {
						insertData(item, file_nextFile);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
