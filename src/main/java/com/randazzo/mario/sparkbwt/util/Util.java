package com.randazzo.mario.sparkbwt.util;


/**
 * This class provide some utility methods.
 * 
 * @author Mario Randazzo
 *
 */
public class Util {
	
	/**
	 * 	Convert an int array to a string, each integer is interpreted as a char.
	 * 
	 * @param s a int array
	 * @return a string that represents s 
	 */
	public static String array2str(int[] s) {
		StringBuilder builder = new StringBuilder();

		for (int value : s) builder.append((char) value);
		
		return builder.toString();
	}
	
	/**
	 * 	Convert a string array to an int array, each char is interpreted as an int
	 * 
	 * @param str a string
	 * @return the integer array correspondent to str
	 */
	public static int[] str2array(String str) {
		int[] result = new int[str.length()];
		
		for(int i = 0; i < str.length(); i++)
			result[i] = str.charAt(i);
		
		return result;
	}

	/**
	 * 	Return the last element of an array.
	 *
	 * @param v a String array
	 * @return the last element of v
	 */
	public static String lastElement(String[] v) {
		return v[v.length - 1];
	}
	
}
