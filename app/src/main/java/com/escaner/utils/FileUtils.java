package com.escaner.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.escaner.entity.ScannerDevice;
import com.moko.support.log.LogModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FileUtils {

    public static File storage;

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return storage + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {column};
        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                null)) {
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    public static void saveProximityList(String fileName, ArrayList<ScannerDevice> saveList) {
        File sdcard = storage;
        try {
            File file = new File(sdcard, "/" + fileName);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fOS = new FileOutputStream(file, false);
            if (isExternalStorageWritable()) {
                fOS.write(("NAME; MAC\n").getBytes());
                for (ScannerDevice aux : saveList) {
                    fOS.write(("" + aux.getName() + "; " + aux.getMac() + "\n").getBytes());
                }
            }
        } catch (IOException ignored) {
        }
    }

    public static void loadProximityList(HashMap<String, ScannerDevice> storeList, String name) throws FileNotFoundException {
        File sdcard = storage;
        File file = new File(sdcard, "/" + name);
        String line;
        Scanner sc = new Scanner(file);
        sc.nextLine();
        ScannerDevice aux;
        while (sc.hasNext() && (line = sc.nextLine()) != null && line.charAt(0) != ';') {
            if (line.charAt(0) != ';') {
                try {
                    aux = parseLine(line);
                } catch (FormatException fe) {
                    aux = null;
                    LogModule.e(fe.getMessage());
                }
                if (aux != null) {
                    storeList.put(aux.getMac(), aux);
                }
            }
        }
    }

    private static ScannerDevice parseLine(String line) throws FormatException {
        String[] params;
        params = line.split(";");
        ScannerDevice ret;
        if (params.length == 2) {
            if (params[1].trim().length() == 17) {
                ret = new ScannerDevice(params[1].trim(), 0, null, 0);
            } else {
                throw new FormatException("Formato de datos incorrecto (Sección <<MAC>> vacía o longitud incorrecta).");
            }
            if (params[0].length() > 0) {
                ret.setName(params[0]);
            } else {
                throw new FormatException("Formato de datos incorrecto (Sección <<ID>> vacía o  de longitud distinta a 8).");
            }
        } else
            throw new FormatException("Formato de datos incorrecto (No hay suficientes columnas).");

        return ret;
    }
}