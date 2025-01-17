/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Joel De La Torriente - jjdltc - https://github.com/jjdltc
 * See a full copy of license in the root folder of the project
 */
package com.jjdltc.cordova.plugin.zip;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class decompressZip {

    private String sourceEntry  = "";
    private String targetPath   = "";
    private final int BUFFER_SIZE = 2048;

    public decompressZip(JSONObject opts) {
        this.sourceEntry    = opts.optString("sourceEntry");
        this.targetPath     = opts.optString("targetPath");
    }
    
    public boolean unZip(){
        boolean result = false;
        try {
            result = this.doUnZip(this.targetPath);
            result = true;
        } catch (IOException e) {
            //result = false;
            result = true;
        }
        return result;
        
    }
    
    /**
     * Extracts a zip file to a given path
     * @param actualTargetPath  Path to un-zip
     * @throws IOException
     */ 
    public boolean doUnZip(String actualTargetPath) throws IOException{
        File target = new File(actualTargetPath);
        if (!target.exists()) {
            target.mkdirs();
        }
        
        ZipInputStream zipFl= new ZipInputStream(new FileInputStream(this.sourceEntry));
        ZipEntry entry      = zipFl.getNextEntry();
        
        while (entry != null) {
            String filePath = actualTargetPath + (actualTargetPath.endsWith("/") ? entry.getName() : File.separator + entry.getName());
            if (entry.isDirectory()) {
                //This part doesn't seem to be fired
                File path = new File(filePath);
                path.mkdir();
            } else {
                //That is why we check here if the target dir exists
                File targetDir = new File(filePath.substring(0, filePath.lastIndexOf("/")));

                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                
                extractFile(zipFl, filePath);
            }
            zipFl.closeEntry();
            entry = zipFl.getNextEntry();
        }
        zipFl.close();
        return true;
    }

    /**
     * Extracts a file
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipFl, String filePath) throws IOException {
        BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[this.BUFFER_SIZE];
        int read = 0;
        while ((read = zipFl.read(bytesIn)) != -1) {
            buffer.write(bytesIn, 0, read);
        }
        buffer.close();
    }
}
