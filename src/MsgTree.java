package msgtree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * to reconstruct/unzip a message archived with a binary-tree-based algorithm
 * 
 * @author Autrin Hakimi
 */
public class MsgTree {

	/**
	 * the payload
	 */
	public char payloadChar;
	/**
	 * the left subtree
	 */
	public MsgTree left;
	/**
	 * the right subtree
	 */
	public MsgTree right; 
	
	/**
	 * the decoding message
	 */
	public static String decodingMsg = "";

	/*
	 * Can use a static char idx to the tree string for recursive solution, but it
	 * is not strictly necessary
	 */
	private static int staticCharIdx = 0;

	public static void main(String[] args) throws FileNotFoundException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please enter filename to decode: ");
		String filename = scanner.next();
		if (!filename.endsWith(".arch")) { // incorrect file format
			return;
		}
		File file = new File(filename);
		scanner.close();
		String encodingString = "";
		Scanner scnr = new Scanner(file);
		int count=0;
		while(scnr.hasNextLine()) {
			count++;
			scnr.nextLine();
		}
		scnr.close();
		Scanner scan = new Scanner(file);
		if(count < 3){
			encodingString = scan.nextLine();
		}	
		else {
			encodingString = scan.nextLine() + "\n" +scan.nextLine();
		}
		MsgTree tree = new MsgTree(encodingString);
		System.out.println("character    code");
		System.out.println("-------------------------");
		MsgTree.printCodes(tree, "");
		System.out.println("MESSAGE:");
		decode(tree, scan.nextLine());
		scan.close();

		//extra credit
		double avgBitsPerChar = (double) encodingString.length() / decodingMsg.length();
		int totalChars = decodingMsg.length();
		double spaceSavings = (1 - ((double) encodingString.length() / (double)(totalChars * 16))) * 100;
		System.out.println("\nSTATISTICS:");
		System.out.printf("Avg bits/char: %.2f \n" , avgBitsPerChar);
		System.out.println("Total characters: " + totalChars);
		System.out.printf("Space savings: %.2f" , spaceSavings);
		System.out.print("%");
	}

	/**
	 * Constructor building the tree from a string
	 * 
	 * @param encodingString
	 */
	public MsgTree(String encodingString) {
		if (staticCharIdx < encodingString.length()) {
			char c = encodingString.charAt(staticCharIdx++);
			if (c == '^') {
				this.left = new MsgTree(encodingString);
				this.right = new MsgTree(encodingString);
			} else {
				this.payloadChar = c;
			}
		}
	}

	/**
	 * Constructor for a single node with null children
	 * 
	 * @param payloadChar the payload
	 */
	public MsgTree(char payloadChar) {
		this.payloadChar = payloadChar;
		left = null;
		right = null;
	}

	/**
	 * method to print characters and their binary codes
	 * 
	 * @param root the root of the tree
	 * @param code the compressed string
	 */
	public static void printCodes(MsgTree root, String code) {
		if (root.left == null && root.right == null) {
			System.out.println(root.payloadChar + "             " + code);

		}else {
			printCodes(root.left, code + "0");
			printCodes(root.right, code + "1");}
	}

	/**
	 * preorder traversal code
	 * @param tree the tree
	 */
	public static void traversePreorder(MsgTree tree) {
		if (tree == null) return;
		System.out.print(tree.toString() + " ");
		traversePreorder(tree.left);
		traversePreorder(tree.right);
	}

	/**
	 * getting the height of the tree
	 * 
	 * @param tree
	 * @return height of the tree
	 */
	public static int height(MsgTree tree) {
		if (tree == null) return -1;
		int leftHeight = height(tree.left);
		int rightHeight = height(tree.right);
		return 1 + Math.max(leftHeight, rightHeight);
	}

	/**
	 * @param tree
	 * @return the size of the tree
	 */
	public static int size(MsgTree tree) {
		if (tree == null) return 0;
		return 1 + size(tree.left) + size(tree.right);
	}

	/**
	 * returns a preorder traversal of the tree as a String
	 * @param tree
	 * @return
	 */
	public static String preorderString(MsgTree tree) {
		StringBuilder sb = new StringBuilder();
		makePreorderString(sb, tree);
		return sb.toString();
	}

	/**
	 * makes a preordered string
	 * @param sb it is a string builder
	 * @param tree the tree
	 */
	private static void makePreorderString(StringBuilder sb, MsgTree tree) {
		sb.append(tree.toString() + " ");
		makePreorderString(sb, tree.left);
		makePreorderString(sb, tree.right);
	}

	/**
   * IGNORE this function
	 * encodes the characters
	 * @param tree
	 * @param encoding the string to be encoded
	 * @return the encoded string
	 */
// 	public static void encode(MsgTree tree, String encoding) {
// 		staticCharIdx=0;
// 		for (int i = 0; i < encoding.length(); i++) {
// 			if (encoding.charAt(staticCharIdx) == '^') {
// 				if (encoding.charAt(staticCharIdx) == '1') {
// 					staticCharIdx++;
// 					encode(tree.right, encoding);
// 				} else if (encoding.charAt(staticCharIdx) == '0') {
// 					staticCharIdx++;
// 					encode(tree.left, encoding);
// 				}
// 			} else {
// 				System.out.print(encoding.charAt(i));
// 				staticCharIdx++;
// 			}
// 		}

// 	}

	/**
	 * decodes the characters
	 * @param tree the tree
	 * @param msg
	 */
	public static void decode(MsgTree tree, String msg) {
		// Start at root
		// Repeat until at leaf
		// Scan one bit
		// Go to left child if 0; else go to right child
		// Print leaf payload

		MsgTree current = tree;
		if(current.left != null || current.right != null) {
			for (int i = 0; i < msg.length(); i++) {
				if (msg.charAt(i) == '0') {
					current = current.left;
					staticCharIdx++;
					if(current.left == null || current.right == null) {
						decodingMsg += current.payloadChar;
						current = tree;
					} 
				}
				else if (msg.charAt(i) == '1') {
					current = current.right;
					staticCharIdx ++;
					if(current.payloadChar != 0) {
						decodingMsg += current.payloadChar;
						current = tree;
					}
				}
			}
			System.out.println(decodingMsg);
		}
	}
}
