package com.test.pachong;

import java.util.ArrayList;



public class Tools {

	public static String getURLFileName(String url) {
		int length = url.lastIndexOf('/');
		String fileName = url.substring(length + 1);

		return fileName;
	}

	public static ArrayList<DownloadThread> downThreads = new ArrayList<DownloadThread>();
	
	public static void main(String[] args)
    {
	    System.err.println(test());
    }

    public static String test() {

        String sb = new String("try");

        try {

            return sb;

        } finally {

            sb += "catch";

        }

    }
}
