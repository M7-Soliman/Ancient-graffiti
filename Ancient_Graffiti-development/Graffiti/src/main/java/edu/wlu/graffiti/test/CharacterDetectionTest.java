package edu.wlu.graffiti.test;

import java.text.Normalizer;
import java.util.ArrayList;

public class CharacterDetectionTest {

	public static void main(String[] args) {
		String testString = "Aristo Tosori (:Tonsori?)";
		String block;
		ArrayList<String> blockLyst = new ArrayList<String>();
		for (int i=0; i < testString.length(); i++) {
			block = Character.UnicodeBlock.of(testString.charAt(i)).toString();
			if (!blockLyst.contains(block)) {
				blockLyst.add(block);
			}
		}		
		System.out.println(blockLyst);
		
		String input = "ὁ";
		String output = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[\\u0300-\\u036F]", "");
		System.out.println(input);
		System.out.println(output);
		System.out.println("o".equals(output));
	}
}
