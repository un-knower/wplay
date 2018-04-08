package com.wplay.core.util;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import java.io.*;

public class ZipUtil {
	private static final int BUFFEREDSIZE = 1024;

	/**
	 * ��ѹzip��ʽ��ѹ���ļ���ָ��λ��
	 *
	 * @param zipFileName
	 *            ѹ���ļ�
	 * @param extPlace
	 *            ��ѹĿ¼
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public synchronized void unzip(String zipFileName, String extPlace)
			throws Exception {
		try {
			(new File(extPlace)).mkdirs();
			File f = new File(zipFileName);
			ZipFile zipFile = new ZipFile(zipFileName);
			if ((!f.exists()) && (f.length() <= 0)) {
				throw new Exception("Ҫ��ѹ���ļ�������!");
			}
			String strPath, gbkPath, strtemp;
			File tempFile = new File(extPlace);
			strPath = tempFile.getAbsolutePath();
			java.util.Enumeration e = zipFile.getEntries();
			while (e.hasMoreElements()) {
				org.apache.tools.zip.ZipEntry zipEnt = (org.apache.tools.zip.ZipEntry) e.nextElement();
				gbkPath = zipEnt.getName();
				if (zipEnt.isDirectory()) {
					strtemp = strPath + File.separator + gbkPath;
					File dir = new File(strtemp);
					dir.mkdirs();
					continue;
				} else {
					// ��д�ļ�
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);
					gbkPath = zipEnt.getName();
					strtemp = strPath + File.separator + gbkPath;

					// ��Ŀ¼
					String strsubdir = gbkPath;
					for (int i = 0; i < strsubdir.length(); i++) {
						if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
							String temp = strPath + File.separator
									+ strsubdir.substring(0, i);
							File subdir = new File(temp);
							if (!subdir.exists())
								subdir.mkdir();
						}
					}
					FileOutputStream fos = new FileOutputStream(strtemp);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int c;
					while ((c = bis.read()) != -1) {
						bos.write((byte) c);
					}
					bos.close();
					fos.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * ��ѹzip��ʽ��ѹ���ļ���ָ��λ��
	 *
	 * @param zipFileName
	 *            ѹ���ļ�
	 * @param extPlace
	 *            ��ѹĿ¼
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public synchronized void unzip(String zipFileName, String extPlace,
			boolean whether) throws Exception {
		try {
			(new File(extPlace)).mkdirs();
			File f = new File(zipFileName);
			ZipFile zipFile = new ZipFile(zipFileName);
			if ((!f.exists()) && (f.length() <= 0)) {
				throw new Exception("Ҫ��ѹ���ļ�������!");
			}
			String strPath, gbkPath, strtemp;
			File tempFile = new File(extPlace);
			strPath = tempFile.getAbsolutePath();
			java.util.Enumeration e = zipFile.getEntries();
			while (e.hasMoreElements()) {
				org.apache.tools.zip.ZipEntry zipEnt = (org.apache.tools.zip.ZipEntry) e.nextElement();
				gbkPath = zipEnt.getName();
				if (zipEnt.isDirectory()) {
					strtemp = strPath + File.separator + gbkPath;
					File dir = new File(strtemp);
					dir.mkdirs();
					continue;
				} else {
					// ��д�ļ�
					InputStream is = zipFile.getInputStream(zipEnt);
					BufferedInputStream bis = new BufferedInputStream(is);
					gbkPath = zipEnt.getName();
					strtemp = strPath + File.separator + gbkPath;

					// ��Ŀ¼
					String strsubdir = gbkPath;
					for (int i = 0; i < strsubdir.length(); i++) {
						if (strsubdir.substring(i, i + 1).equalsIgnoreCase("/")) {
							String temp = strPath + File.separator
									+ strsubdir.substring(0, i);
							File subdir = new File(temp);
							if (!subdir.exists())
								subdir.mkdir();
						}
					}
					FileOutputStream fos = new FileOutputStream(strtemp);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					int c;
					while ((c = bis.read()) != -1) {
						bos.write((byte) c);
					}
					bos.close();
					fos.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * ѹ��zip��ʽ��ѹ���ļ�
	 *
	 * @param inputFilename
	 *            ѹ�����ļ����ļ��м���ϸ·��
	 * @param zipFilename
	 *            ����ļ����Ƽ���ϸ·��
	 * @throws IOException
	 */
	public synchronized void zip(String inputFilename, String zipFilename)
			throws IOException {
		zip(new File(inputFilename), zipFilename);
	}

	/**
	 * ѹ��zip��ʽ��ѹ���ļ�
	 *
	 * @param inputFile
	 *            ��ѹ���ļ�
	 * @param zipFilename
	 *            ����ļ�����ϸ·��
	 * @throws IOException
	 */
	public synchronized void zip(File inputFile, String zipFilename)
			throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));
		try {
			zip(inputFile, out, "");
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	/**
	 * ѹ��zip��ʽ��ѹ���ļ�
	 *
	 * @param inputFile
	 *            ��ѹ���ļ�
	 * @param out
	 *            ���ѹ���ļ�
	 * @param base
	 *            ������ʶ
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private synchronized void zip(File inputFile, ZipOutputStream out,
			String base) throws IOException {
		if (inputFile.isDirectory()) {
			File[] inputFiles = inputFile.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < inputFiles.length; i++) {
				zip(inputFiles[i], out, base + inputFiles[i].getName());
			}
		} else {
			if (base.length() > 0) {
				out.putNextEntry(new ZipEntry(base));
			} else {
				out.putNextEntry(new ZipEntry(inputFile.getName()));
			}
			FileInputStream in = new FileInputStream(inputFile);
			try {
				int c;
				byte[] by = new byte[BUFFEREDSIZE];
				while ((c = in.read(by)) != -1) {
					out.write(by, 0, c);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				in.close();
			}
		}
	}
	
	public synchronized void zip(FileSystem fs,Path inputFile, String zipFilename)throws IOException{
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFilename));
		try {
			zip(fs,inputFile, out, "");
		} finally {
			out.close();
		}
	}
	
	/**
	 * 
	 * @param src
	 * @param out
	 * @param base
	 * @throws IOException
	 */
	private void zip(FileSystem fs,Path src,ZipOutputStream out,String base)throws IOException{
		FileStatus fStatus = fs.getFileStatus(src);
		if (fStatus.isDir()) {
			FileStatus[] fStatuses = fs.listStatus(src);
			out.putNextEntry(new ZipEntry(base + "/"));
			out.closeEntry();
			base = base.length() == 0 ? "/" : base + "/";
			for (FileStatus fileSt : fStatuses) {
				zip(fs,fileSt.getPath(), out, base + fileSt.getPath().getName());
			}
		} else {
			if (base.length() > 0) {
				out.putNextEntry(new ZipEntry(base));
			} else {
				out.putNextEntry(new ZipEntry(src.getName()));
			}
			InputStream in = fs.open(src);
			StreamUtil.output(in, out,false);
			out.closeEntry();
		}
	}
	
}
