package ru.ncedu.wortellen.utils2.archive_comparator;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ArchiveComparator {
    String archivename1;
    String archivename2;
    long[] filesize1 =new long[0];
    long[] filesize2 =new long[0];
    String[] filename1= new String[0];
    String[] filename2= new String[0];
    public void Comparator(){
        selectArchives();
        ReadingFirstArchive(archivename1);
        ReadingSecondArchive(archivename2);
        isSameToFile();
        System.out.println("Completed");
    }

    private void selectArchives(){
        Scanner sc =new Scanner(System.in);
        System.out.println("Enter first archive or press \"Enter\" to open dialog form");
        archivename1 = sc.nextLine();
        if(archivename1.equals(""))
        {
            System.out.println("Choose archives:");
            archivename1 =fileChooser(1);
            archivename2 =fileChooser(2);
        }
        else {
            System.out.println("Enter second archive");
            archivename2 = sc.nextLine();
        }
    }
    private String fileChooser(int num){
        JButton open = new JButton();
        JFileChooser fc = new JFileChooser(".");
        fc.setDialogTitle("Choose Archive "+num);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.showOpenDialog(open);
        return fc.getSelectedFile().getName();
    }
    private void ReadingFirstArchive(String zipArchiveName){
        try(ZipInputStream zin = new ZipInputStream(new FileInputStream(zipArchiveName)))
        {
            ZipEntry entry;
            int i = 0;
            System.out.println("Archive 1");
            while((entry=zin.getNextEntry())!=null){
                filename1 =extendArraySizeStr(filename1);
                filesize1 = extendArraySizeLong(filesize1);
                filename1[i] = entry.getName();
                FileOutputStream fout = new FileOutputStream(filename1[i]);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                filesize1[i]=entry.getSize();  // получим его размер в байтах
                System.out.printf("File name: %s \t File size: %d \n", filename1[i], filesize1[i]);
                i++;
                fout.flush();
                zin.closeEntry();
                fout.close();

            }
        }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }
    }

    private void ReadingSecondArchive(String zipArchiveName){
        try(ZipInputStream zin = new ZipInputStream(new FileInputStream(zipArchiveName)))
        {
            ZipEntry entry;
            int i = 0;
            System.out.println("Archive 2");
            while((entry=zin.getNextEntry())!=null){
                filename2 =extendArraySizeStr(filename2);
                filesize2 = extendArraySizeLong(filesize2);
                filename2[i] = entry.getName();
                FileOutputStream fout = new FileOutputStream(filename2[i]);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                filesize2[i]=entry.getSize();
                System.out.printf("File name: %s \t File size: %d \n", filename2[i], filesize2[i]);
                i++;
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        }
        catch(Exception ex){

            System.out.println(ex.getMessage());
        }

    }
    private void isSameToFile(){
        try(FileWriter writer = new FileWriter("Results.txt", false))
        {
            writer.write(String.format("%-20s|%-20s\n","Archive1","Archive2"));
            writer.write("--------------------+--------------------\n");
            int deleteflag=0; //for check deleted files
            int[] newflag = new int[filesize2.length]; // for check new files in second archive
            for (int i=0;i<filesize1.length;i++) {
                for (int j = 0; j < filesize2.length; j++) {
                    if ((filesize1[i] == filesize2[j]) && (filename1[i].equals(filename2[j]))) {
                        writer.write(String.format("  %-18s|  %-18s\n",filename1[i],filename2[j]));
                        newflag[j] = 1;
                        j = filesize2.length;

                    } else if ((filesize2[j] != filesize1[i]) && (filename1[i].equals(filename2[j]))) {
                        writer.write(String.format("* %-18s|* %-18s\n",filename1[i],filename2[j]));
                        newflag[j] = 1;
                        j = filesize2.length;
                    } else if (filesize1[i] == filesize2[j]) {
                        writer.write(String.format("? %-18s|? %-18s\n",filename1[i],filename2[j]));
                        newflag[j] = 1;
                    } else {
                        deleteflag++;
                    }
                }
                if (deleteflag == filesize2.length) {
                    writer.write(String.format("- %-18s|\n",filename1[i]));
                }
                deleteflag = 0;
            }
            for(int i=0;i<newflag.length;i++)
                if(newflag[i]!=1)
                    writer.write(String.format("%20s|+ %-18s\n","",filename2[i]));
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private  String[] extendArraySizeStr(String[] array){
        String [] temp = array.clone();
        array = new String[array.length + 1];
        System.arraycopy(temp, 0, array, 0, temp.length);
        return array;
    }
    private  long[] extendArraySizeLong(long[] array){
        long [] temp = array.clone();
        array = new long[array.length + 1];
        System.arraycopy(temp, 0, array, 0, temp.length);
        return array;
    }
}
