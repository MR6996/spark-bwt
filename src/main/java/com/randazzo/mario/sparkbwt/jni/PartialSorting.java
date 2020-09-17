package com.randazzo.mario.sparkbwt.jni;

import cz.adamh.utils.NativeUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.Serializable;

/**
 * 	This class provide a JNI for the functions that calculate Partial Suffix Array.
 * 
 * @author Mario Randazzo
 *
 */
public class PartialSorting implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 5251326424419860278L;

	//Load shared library
	static {
		try {

			if(SystemUtils.IS_OS_WINDOWS)
				NativeUtils.loadLibraryFromJar("/partial.dll");
			else if(SystemUtils.IS_OS_LINUX)
				NativeUtils.loadLibraryFromJar("/partial.so");
			else
				throw new Exception("Can't load sapartial library. Error in OS detection.");
		} catch (IOException e) {
			System.out.println("Can't load sapartial library. I/O Error.");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 *
	 * @param s a string
	 * @param p
	 * @param pSorted
	 * @param k the size of the alphabet of s
	 */
	public native static void calculatePartialSA(int[] s, int[] p, int[] pSorted, int k);
	
	
	/**
	 * 
	 * 
	 * @param s a string
	 * @param p
	 * @param k the size of the alphabet of s
	 * @return
	 */
	public static int[] calculatePartialSA(String s, int[] p, int k) {
		int[] pSorted = new int[p.length];
		System.out.println("ciao");
		
		calculatePartialSA(PartialSorting.str2array(s), p, pSorted, k);
		
		return pSorted;
	}

	/**
	 * 	Convert a string array to an int array, each char is interpreted as an int
	 *
	 * @param str a string
	 * @return the integer array correspondent to str
	 */
	private static int[] str2array(String str) {
		int[] result = new int[str.length()];

		for(int i = 0; i < str.length(); i++)
			result[i] = str.charAt(i);

		return result;
	}

}