package prj02;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import HashTable.*;
import List.*;
import SortedList.*;
import Tree.*;


/**
 * The Huffman Encoding Algorithm
 *
 * This is a data compression algorithm designed by David A. Huffman and published in 1952
 *
 * What it does is it takes a string and by constructing a special binary tree with the frequencies of each character.
 * This tree generates special prefix codes that make the size of each string encoded a lot smaller, thus saving space.
 *
 * @author Fernando J. Bermudez Medina (Template)
 * @author A. ElSaid (Review)
 * @author Adrian Irizarry Negron <802-19-8275> (Implementation)
 * @version 2.0
 * @since 10/16/2021
 */
public class HuffmanCoding {

	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		String data = load_data("input1.txt"); //You can create other test input files and add them to the inputData Folder

		/*If input string is not empty we can encode the text using our algorithm*/
		if(!data.isEmpty()) {
			Map<String, Integer> fD = compute_fd(data);
			BTNode<Integer,String> huffmanRoot = huffman_tree(fD);
			Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
			String output = encode(encodedHuffman, data);
			process_results(fD, encodedHuffman,data,output);
		} else {
			System.out.println("Input Data Is Empty! Try Again with a File that has data inside!");
		}

	}

	/**
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 *
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/*If input file is empty just return an empty string, if not just extract the data*/
			String extracted = in.readLine();
			if(extracted != null)
				line = extracted;

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	/**
	 * Receives a string named inputString and returns a map which maps each character to the frequency
	 * in which they appear.
	 *
	 * @param inputString name of the string to be processed
	 * @return Map with the frequency distribution of each character of the inputString
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		Map<String, Integer> freq = new HashTableSC<String, Integer>(new SimpleHashFunction<>());
		/* We iterate through the string to go through each letter and store them in the map (freq). */
		for (int i = 0; i < inputString.length();i++) {
			String result = inputString.substring(i,i+1);
			/* If the character is not already in the map then we add it */
			if (freq.get(result) == null) {
				freq.put(result,1);
			/* If the character is already in the map we add one to the corresponding value  */
			}else {
				freq.put(result,freq.get(result) + 1);
			}

		}
		return freq;
	}

	/**
	 * Constructs a huffman tree with the help of a map (fD) which contains the frequency distribution of
	 * each character and a sorted linked list which helps organize the frequency from min to max. This function then
	 * takes the characters with the least frequency and adds them up creating a parent node. Like this we create
	 * a tree from the leafs up to the root of the tree.
	 *
	 * @param fD Map that contains the amount of times a character appears in a given string.
	 * @return Root node of the huffman tree
	 */
	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> fD) {
		/* Create a sorted linked list so we can organize each character frequency by order, from least frequent to most frequent */
		SortedList<BTNode<Integer,String>> sortedFreq = new SortedLinkedList<BTNode<Integer,String>>();
		for (String key : fD.getKeys()) {
			BTNode<Integer,String> node = new BTNode<Integer,String>(fD.get(key),key);
			sortedFreq.add(node);
		}
		/* Iterate through the sorted list taking the two least frequent nodes connecting them to a parent which
		*  will be the sum of the two characters and frequency.
		*/
		BTNode<Integer,String> temp = new BTNode<Integer,String>();
		for (int i = 0; i <= sortedFreq.size()-1; i++) {
			BTNode<Integer,String> newNode = new BTNode<>();
			newNode.setLeftChild(sortedFreq.removeIndex(0));
			newNode.setRightChild(sortedFreq.removeIndex(0));
			/* We set the value and key of the parent to the sum of the children node */
			newNode.setValue(newNode.getLeftChild().getValue() + newNode.getRightChild().getValue());
			newNode.setKey(newNode.getLeftChild().getKey() + newNode.getRightChild().getKey());
			sortedFreq.add(newNode);
		}
		/* Iterate through the remaining nodes, taking the two least frequent and connecting them to a parent which
		*  which will be the sum of these resulting at the end in one node which will be the root of the tree.
		*/
		while (sortedFreq.size() != 1) {
			BTNode<Integer,String> newNode = new BTNode<>();
			newNode.setLeftChild(sortedFreq.removeIndex(0));
			newNode.setRightChild(sortedFreq.removeIndex(0));
			newNode.setValue(newNode.getLeftChild().getValue() + newNode.getRightChild().getValue());
			newNode.setKey(newNode.getLeftChild().getKey() + newNode.getRightChild().getKey());
			sortedFreq.add(newNode);
		}


		return sortedFreq.removeIndex(0); // Returns the root of the tree.
	}

	/**
	 * Helper function for huffman_code that will traverse the huffman tree in inorder traversal, each time that it finds
	 * a character it will save the prefix of the character in a map which key is the character and the value it's prefix.
	 *
	 * @param root BTNode that is the root of the previously created huffman tree
	 * @param result Map that will contain the prefix for each character
	 * @param prefix String that will contain the code for each character on the tree.
	 */
	public static void huffmanHelper(BTNode<Integer,String> root,Map<String, String> result,String prefix) {
		if (root == null) {
			return;
		}
		/* If the length of the String is 1 it means we found a character, therefore we save the character and its prefix. */
		if (root.getValue().length() == 1) {
			result.put(root.getValue(), prefix);
		}
		/* The huffman coding algorithm lets us know that each time we go left we add 0 to the prefix, each time we go right we add 1 */
		huffmanHelper(root.getLeftChild(),result,prefix + "0");
		huffmanHelper(root.getRightChild(),result,prefix + "1");

	}

	/**
	 * Traverses the given huffman tree with the help of a helper function, huffmanHelper, and returns a map with the character as it's key
	 * and their corresponding huffman code or prefix.
	 *
	 * @param huffmanRoot Root of the huffman tree
	 * @return Map that contains the character as it's key and the prefix as it's value
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> huffmanRoot) {

		Map<String, String> result = new HashTableSC<String, String>(new SimpleHashFunction<>());
		/* Call the helper function that will traverse the tree and store the corresponding huffman code values in the map.
		*  We pass it the root of the tree, the map and a empty string.
		*/
		huffmanHelper(huffmanRoot,result,"");
		return result;
	}



	/**
	 * Receives the encoding map and inputString and encodes the input string following the huffman coding algorithm.
	 * This is done with the encodingMap which contains the huffman code for each character.
	 *
	 * @param encodingMap Map that contains the huffman code for each character of the inputString.
	 * @param inputString String to be encoded.
	 * @return String that has been encoded following the huffman coding algorithm.
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) {
		String result = "";
		/*
		* Iterate through the inputString so we can look at each character of the string.
		* For each character we get the corresponding value of the character and we add it to the result string.
		*/
		for (int i = 0;i < inputString.length(); i++) {
			String temp = inputString.substring(i,i+1);
			result += encodingMap.get(temp);
		}
		return result;
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable, the input string,
	 * and the output string, and prints the results to the screen (per specifications).
	 *
	 * Output Includes: symbol, frequency and code.
	 * Also includes how many bits has the original and encoded string, plus how much space was saved using this encoding algorithm
	 *
	 * @param fD Frequency Distribution of all the characters in input string
	 * @param encodedHuffman Prefix Code Map
	 * @param inputData text string from the input file
	 * @param output processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String output) {
		/*To get the bytes of the input string, we just get the bytes of the original string with string.getBytes().length*/
		int inputBytes = inputData.getBytes().length;

		/**
		 * For the bytes of the encoded one, it's not so easy.
		 *
		 * Here we have to get the bytes the same way we got the bytes for the original one but we divide it by 8,
		 * because 1 byte = 8 bits and our huffman code is in bits (0,1), not bytes.
		 *
		 * This is because we want to calculate how many bytes we saved by counting how many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes will give us how much space we "chopped off"
		 *
		 * So we have to subtract that "chopped off" percentage to the total (which is 100%)
		 * and that's the difference in space required
		 */
		String savings =  d.format(100 - (( (float) (outputBytes / (float)inputBytes) ) * 100));


		/**
		 * Finally we just output our results to the console
		 * with a more visual pleasing version of both our Hash Tables in decreasing order by frequency.
		 *
		 * Notice that when the output is shown, the characters with the highest frequency have the lowest amount of bits.
		 *
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer,String>> sortedList = new SortedLinkedList<BTNode<Integer,String>>();

		/* To print the table in decreasing order by frequency, we do the same thing we did when we built the tree
		 * We add each key with it's frequency in a node into a SortedList, this way we get the frequencies in ascending order*/
		for (String key : fD.getKeys()) {
			BTNode<Integer,String> node = new BTNode<Integer,String>(fD.get(key),key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order,
		 * we just traverse the list backwards and start printing the nodes key (character) and value (frequency)
		 * and find the same key in our prefix code "Lookup Table" we made earlier on in huffman_code().
		 *
		 * That way we get the table in decreasing order by frequency
		 * */
		for (int i = sortedList.size() - 1; i >= 0; i--) {
			BTNode<Integer,String> node = sortedList.get(i);
			System.out.println(node.getValue() + "\t" + node.getKey() + "\t    " + encodedHuffman.get(node.getValue()));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output);
		System.out.println("Decoded String: \n" + decodeHuff(output, encodedHuffman) + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + (int) outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}


	/*************************************************************************************
	 ** ADD ANY AUXILIARY METHOD YOU WISH TO IMPLEMENT TO FACILITATE YOUR SOLUTION HERE **
	 *************************************************************************************/

	/**
	 * Auxiliary Method that decodes the generated string by the Huffman Coding Algorithm
	 *
	 * Used for output Purposes
	 *
	 * @param output - Encoded String
	 * @param lookupTable
	 * @return The decoded String, this should be the original input string parsed from the input file
	 */
	public static String decodeHuff(String output, Map<String, String> lookupTable) {
		String result = "";
		int start = 0;
		List<String>  prefixCodes = lookupTable.getValues();
		List<String> symbols = lookupTable.getKeys();

		/*looping through output until a prefix code is found on map and
		 * adding the symbol that the code that represents it to result */
		for(int i = 0; i <= output.length();i++){

			String searched = output.substring(start, i);

			int index = prefixCodes.firstIndex(searched);

			if(index >= 0) { //Found it
				result= result + symbols.get(index);
				start = i;
			}
		}
		return result;
	}


}
