package com.wplay.core.util;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPOutputStream;


/* 
 * ���ܣ�ѹ���ļ���tar.gz��ʽ
 */
public class TarUtils {
 private static int BUFFER = 1024 * 4; // �����С
 private static byte[] B_ARRAY = new byte[BUFFER];

 /*
  * �������ܣ���������ļ����ļ��� ������inputFileName Ҫ������ļ��л��ļ���·�� targetFileName �������ļ�·��
  */
 public void execute(String inputFileName, String targetFileName) {
  File inputFile = new File(inputFileName);
  String base = inputFileName
    .substring(inputFileName.lastIndexOf("/") + 1);
  TarOutputStream out = getTarOutputStream(targetFileName);
  tarPack(out, inputFile, base);
  try {
   if (null != out) {
    out.close();
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
  compress(new File(targetFileName));
 }

 /*
  * �������ܣ��������ļ����ļ��� ������inputFileNameList Ҫ������ļ��л��ļ���·�����б� targetFileName
  * �������ļ�·��
  */
 public void execute(List<String> inputFileNameList, String targetFileName) {
  TarOutputStream out = getTarOutputStream(targetFileName);

  for (String inputFileName : inputFileNameList) {
   File inputFile = new File(inputFileName);
   String base = inputFileName.substring(inputFileName
     .lastIndexOf("/") + 1);
   tarPack(out, inputFile, base);
  }

  try {
   if (null != out) {
    out.close();
   }
  } catch (IOException e) {
   e.printStackTrace();
  }
//  compress(new File(targetFileName));
 }

 /*
  * �������ܣ������tar�ļ� ������out ����������ļ����� inputFile Ҫѹ�����ļ��л��ļ� base ����ļ��е�·��
  */

 private void tarPack(TarOutputStream out, File inputFile, String base) {
  if (inputFile.isDirectory()) // ����ļ���
  {
   packFolder(out, inputFile, base);
  } else // ����ļ�
  {
   packFile(out, inputFile, base);
  }
 }

 /*
  * �������ܣ������ļ����µ����ݣ���������ļ��У��͵���tarPack���� ������out ����������ļ����� inputFile Ҫѹ�����ļ��л��ļ�
  * base ����ļ��е�·��
  */
 private void packFolder(TarOutputStream out, File inputFile, String base) {
  File[] fileList = inputFile.listFiles();
  try {
   // �ڴ���ļ������·��
   out.putNextEntry(new TarEntry(base + "/"));
  } catch (IOException e) {
   e.printStackTrace();
  }
  base = base.length() == 0 ? "" : base + "/";
  for (File file : fileList) {
   tarPack(out, file, base + file.getName());
  }
 }

 /*
  * �������ܣ�����ļ� ������out ѹ���������ļ����� inputFile Ҫѹ�����ļ��л��ļ� base ����ļ��е�·��
  */
 private void packFile(TarOutputStream out, File inputFile, String base) {
  TarEntry tarEntry = new TarEntry(base);

  // ���ô���ļ��Ĵ�С����������ã���������ݵ��ļ�ʱ���ᱨ��
  System.out.println(inputFile + " len = " + inputFile.length());
  tarEntry.setSize(inputFile.length());
  try {
   out.putNextEntry(tarEntry);
  } catch (IOException e) {
   e.printStackTrace();
  }
  FileInputStream in = null;
  try {
   in = new FileInputStream(inputFile);
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  }
  int b = 0;

  try {
   while ((b = in.read(B_ARRAY, 0, BUFFER)) != -1) {
    out.write(B_ARRAY, 0, b);
   }
  } catch (IOException e) {
   e.printStackTrace();
  } catch (NullPointerException e) {
   System.err
     .println("NullPointerException info ======= [FileInputStream is null]");
  } finally {
   try {
    if (null != in) {
     in.close();
    }
    if (null != out) {
     out.closeEntry();
    }
   } catch (IOException e) {

   }
  }
 }

 /*
  * �������ܣ��Ѵ����tar�ļ�ѹ����gz��ʽ ������srcFile Ҫѹ����tar�ļ�·��
  */
 private void compress(File srcFile) {
  File target = new File(srcFile.getAbsolutePath() + ".gz");
  FileInputStream in = null;
  GZIPOutputStream out = null;
  try {
   in = new FileInputStream(srcFile);
   out = new GZIPOutputStream(new FileOutputStream(target));
   int number = 0;
   while ((number = in.read(B_ARRAY, 0, BUFFER)) != -1) {
    out.write(B_ARRAY, 0, number);
   }
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  } catch (IOException e) {
   e.printStackTrace();
  } finally {
   try {
    if (in != null) {
     in.close();
    }

    if (out != null) {
     out.close();
    }
   } catch (IOException e) {
    e.printStackTrace();
   }
  }
 }

 /*
  * �������ܣ���ô�����ļ����� ������targetFileName ������ļ���·��
  */
 private TarOutputStream getTarOutputStream(String targetFileName) {
  // �������ļ�û��.tar��׺�������Զ�����
  targetFileName = targetFileName.endsWith(".tar") ? targetFileName
    : targetFileName + ".tar";
  FileOutputStream fileOutputStream = null;
  try {
   fileOutputStream = new FileOutputStream(targetFileName);
  } catch (FileNotFoundException e) {
   e.printStackTrace();
  }
  BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
    fileOutputStream);
  TarOutputStream out = new TarOutputStream(bufferedOutputStream);

  // �������������Σ���ѹ�����е�·���ֽ�������100 byteʱ���ͻᱨ��
  out.setLongFileMode(TarOutputStream.LONGFILE_GNU);
  return out;
 }

}