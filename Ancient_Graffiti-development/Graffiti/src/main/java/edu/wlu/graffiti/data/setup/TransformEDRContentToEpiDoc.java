/**
 * 
 */
package edu.wlu.graffiti.data.setup;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Transforms the content in the EDR conventions into EpiDoc content
 * 
 * @author Trevor Stalnaker
 * @author Sara Sprenkle
 */
public class TransformEDRContentToEpiDoc {
	private static Map<String, String> charMap = new HashMap<String, String>();

	static {
		buildMap();
	}

	/**
	 * Main driver of transforming the EDR-style content into EpiDoc content
	 * @param content - the EDR-style content
	 * @return
	 */
	public static String transformContentToEpiDoc(String content) {
		Pattern pattern;
		Matcher matcher;

		content = normalize(content);

		// Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:textus non legitur\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIllegibleText(content);
		}

		// Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:vacat\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addBlankSpace(content);
		}

		// Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:ianua\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addDoorSpace(content);
		}

		// Must be done before we determine line breaks
		pattern = Pattern.compile("\\[([ ]?-[ ]?){6}\\](\\n\\[([ ]?-[ ]?){6}\\])*");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addLostLinesExtentKnown(content);
		}

		if (content.contains(":columna")) { // if content is split across columns, mark those columns
			content = markContentWithColumns(content);
		} else {
			content = addLBTagsToContent(content);
		}

		// Must use HTML entities to search for angle brackets
		pattern = Pattern.compile("\\&\\#60\\;\\:([^\\s]+)\\&\\#62\\;");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSubaudible(content);
		}

		pattern = Pattern.compile("\\(\\([^\\(\\)\\:]+\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSymbols(content);
		}

		pattern = Pattern.compile("\\(\\(\\:[^\\[\\]\\:\\<\\>]*\\)\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = markContentWithFigureTags(content);
		}

		// With a few tweeks this could be used in place of the other checks, but for
		// now it is an addition
		pattern = Pattern.compile("\\[[^\\[\\]]+\\]");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSquareBrackets(content);
		}

		// Temporary Fix Implemented here!
		// pattern = Pattern.compile("[^\\s\\>]*[ ]?\\(\\:[^\\s\\?]*\\)");
		pattern = Pattern.compile("[^\\s\\>\\]]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addNonStandardSpelling(content);
		}

		pattern = Pattern.compile("[^\\s\\>]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addUncertainNonStandardSpelling(content);
		}

		pattern = Pattern.compile("([^\\s>]+)\\((\\-[ ]?){3}[ ]?\\??\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addUnknownAbbreviationTags(content);
		}

		pattern = Pattern.compile("(?<! )[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addAbbreviationTags(content);
		}

		pattern = Pattern
				.compile("(?<! )[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addAbbreviationTagsWithUncertainty(content);
		}

		// pattern = Pattern.compile("\\[\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]\\]");
		// This removed too many nested characters (like [])
		pattern = Pattern.compile("(\\[\\[|〚)([^\\s\\(\\[\\)\\]\\:\\<\\>\\,]|\\[?\\]?)*(\\]\\]|〛)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIntentionallyErasedTags(content);
		}

		pattern = Pattern.compile("\\++");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIllegibleCharacters(content);
		}

		pattern = Pattern.compile("([A-Z][\u0332,\u0323,\u0302]?){2,}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addIncomprehensibleCharacters(content);
		}

		pattern = Pattern.compile("(([^\\s]\u0302)+)([^\\s](\u0323|\u0332)?)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addLettersJoinedInLigature(content);
		}

		pattern = Pattern.compile("(- - - - - -)|(------)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addLostLines(content);
		}

		pattern = Pattern.compile("([^\\s]\u0332)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addOnceVisibleNowMissingCharacters(content);
		}

		pattern = Pattern.compile("([^\\s]\u0323)+");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addDamagedCharacters(content);
		}

		pattern = Pattern.compile("\\{[^\\s\\{\\}]*\\}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSurplusCharacters(content);
		}

		pattern = Pattern.compile("(?<= )\\([^\\s:]+\\)");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addSubAudibleSymbols(content);
		}

		pattern = Pattern.compile("[ ]?=[\\s]*\\<lb n=[\\\"']([\\d])+[\\\"']/\\>");
		matcher = pattern.matcher(content);
		if (matcher.find()) {
			content = addWordBreaks(content);
		}

		return content;
	}
	
	/**
	 * Adds mark up for various types of line breaks in the content
	 * 
	 * @param content
	 * @return
	 */
	private static String addLBTagsToContent(String content) {
		StringBuilder returnString = new StringBuilder();
		String[] splitContent = content.split("\n *");
		int n = 1;
		for (int i = 0; i < splitContent.length; i++) {
			// returnString.append("<lb n='" + Integer.toString(n) + "'");
			Matcher matcher = Pattern.compile("\\&\\#60\\;\\:ad perpendiculum\\&\\#62\\;").matcher(content);
			if (matcher.find()) {
				if (!Pattern.compile("\\&\\#60\\;\\:ad perpendiculum\\&\\#62\\;").matcher(splitContent[i]).find()) {
					returnString.append("<lb n='" + Integer.toString(n) + "'");
					returnString.append(" style='text-direction:vertical'/>"
							+ splitContent[i].replaceAll("\\&\\#60\\;\\:ad perpendiculum\\&\\#62\\;", ""));
				} else {
					n--;
				}

			} else {
				returnString.append("<lb n='" + Integer.toString(n) + "'/>" + splitContent[i]);
			}
			n++;
		}
		return returnString.toString().trim();
	}

	/**
	 * Adds mark up for abbreviations of the form: ab(c)
	 * 
	 * @param content
	 * @return
	 */
	private static String addAbbreviationTags(String content) {
		String temp;
		Pattern pattern = Pattern
				.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String abbr = temp.split("\\(")[0];
			String ex = temp.split("\\(")[1].split("\\)")[0];
			content = content.replace(temp, "<expan><abbr>" + abbr + "</abbr><ex>" + ex + "</ex></expan>");
		}
		return content;
	}

	/**
	 * Adds mark up for abbreviations where the expansion is uncertain
	 * 
	 * @param content
	 * @return
	 */
	private static String addAbbreviationTagsWithUncertainty(String content) {
		String temp;
		Pattern pattern = Pattern
				.compile("[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\([^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]+\\?\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String abbr = temp.split("\\(")[0];
			String ex = temp.split("\\(")[1].split("\\?\\)")[0];
			content = content.replace(temp, "<expan><abbr>" + abbr + "</abbr><ex cert='low'>" + ex + "</ex></expan>");
		}
		return content;
	}

	/**
	 * Adds mark up for characters that were intentionally removed
	 * 
	 * @param content
	 * @return
	 */
	private static String addIntentionallyErasedTags(String content) {
		String temp;
		// Pattern pattern =
		// Pattern.compile("\\[\\[[^\\s\\(\\[\\)\\]\\:\\<\\>\\?\\,]*\\]\\]");
		Pattern pattern = Pattern.compile("(\\[\\[|〚)([^\\s\\(\\[\\)\\]\\:\\<\\>\\,]|\\[?\\]?)*(\\]\\]|〛)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<del rend='erasure'><supplied reason='lost'>"
					+ temp.replaceAll("\\[\\[|\\]\\]|〚|〛", "") + "</supplied></del>");
		}
		return content;
	}

	/**
	 * Epidocifies non-standard spellings
	 * 
	 * @param content
	 * @return
	 */
	private static String addNonStandardSpelling(String content) {
		String temp;
		// Pattern pattern = Pattern.compile("[^\\s\\>]*[ ]?\\(\\:[^\\s\\?]*\\)");
		// //Allow for only one space between elements
		// Pattern pattern = Pattern.compile("[^\\s\\>]*[ ]*\\(\\:[^\\s\\?]*\\)");
		// //Disallow spaces with parens
		Pattern pattern = Pattern.compile("[^\\s\\>\\]]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String orig = temp.split("[ ]?\\(\\:")[0];
			String reg = temp.split("\\(\\:")[1].split("\\)")[0];
			content = content.replace(temp, "<choice><reg>" + reg + "</reg><orig>" + orig + "</orig></choice>");
		}
		return content;
	}

	/**
	 * Epidocifies uncertain non-standard spellings
	 * 
	 * @param content
	 * @return
	 */
	private static String addUncertainNonStandardSpelling(String content) {
		String temp;
		Pattern pattern = Pattern.compile("[^\\s\\>]+[ ]*\\(\\:[^\\t\\n\\?\\)]+\\?\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String orig = temp.split("[ ]?\\(\\:")[0];
			String reg = temp.split("\\(\\:")[1].split("\\)")[0].replaceAll("\\?", "");
			content = content.replace(temp,
					"<choice><reg cert='low'>" + reg + "</reg><orig>" + orig + "</orig></choice>");
		}
		return content;
	}

	/**
	 * Epidocifies illegible characters
	 * 
	 * @param content
	 * @return
	 */
	private static String addIllegibleCharacters(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\++");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String quant = ((Integer) (temp.length())).toString();
			content = content.replaceFirst("\\++",
					"<gap reason='illegible' quantity='" + quant + "' unit='character'/>");
		}
		return content;
	}

	/**
	 * Epidocifies letters joined in ligature
	 * 
	 * @param content
	 * @return
	 */
	private static String addLettersJoinedInLigature(String content) {
		// carot = "\u0302"
		String temp;
		Pattern pattern = Pattern.compile("(([^\\s]\u0302)+)([^\\s](\u0323|\u0332)?)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String tagString = "";
			String[] joinedChars = matcher.group(1).split("\u0302");
			for (int i = 0; i < joinedChars.length; i++) {
				tagString += joinedChars[i];
			}
			tagString += matcher.group(3);
			content = content.replaceFirst(temp, "<hi rend='ligature'>" + tagString + "</hi>");
		}
		return content;
	}

	/**
	 * Epidocifies lost lines
	 * 
	 * @param content
	 * @return
	 */
	private static String addLostLines(String content) {
		String temp;
		Pattern pattern = Pattern.compile("(- - - - - -)|(------)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<gap reason='lost' extent='unknown' unit='line'/>");
		}
		return content;
	}

	/**
	 * Epidocifies characters formerly visible, now missing
	 * 
	 * @param content
	 * @return
	 */
	private static String addOnceVisibleNowMissingCharacters(String content) {
		// macron = \u0331 or combining low line = \u0332
		String temp;
		Pattern pattern = Pattern.compile("([^\\s]\u0332)+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replaceFirst(temp, "<supplied reason='undefined' evidence='previouseditor'>"
					+ temp.replaceAll("\u0332", "") + "</supplied>");
		}
		return content;
	}

	/**
	 * Epidocifies characters damaged or unclear without context
	 * 
	 * @param content
	 * @return
	 */
	private static String addDamagedCharacters(String content) {
		// dot = \u0323
		String temp;
		Pattern pattern = Pattern.compile("([^\\s]\u0323)+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replaceFirst(temp, "<unclear>" + temp.replaceAll("\u0323", "") + "</unclear>");
		}
		return content;
	}

	/**
	 * Epidocifies surplus characters
	 * 
	 * @param content
	 * @return
	 */
	private static String addSurplusCharacters(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\{[^\\s\\{\\}]*\\}");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<surplus>" + temp.replaceAll("\\{|\\}", "") + "</surplus>");
		}
		return content;
	}

	private static String markContentWithFigureTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\(\\(\\:[^\\[\\]\\:\\<\\>]*\\)\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<figure><figDesc>"
					+ temp.replaceAll("\\(\\(\\:", "").replaceAll("\\)\\)", "") + "</figDesc></figure>");
		}
		return content;
	}

	/**
	 * Adds appropriate mark up for columns in the content
	 * 
	 * @param content
	 * @return
	 */
	private static String markContentWithColumns(String content) {
		StringBuilder returnString = new StringBuilder();
		String[] splitContentAcrossColumns = content.split(".*columna.*");
		for (int i = 1; i < splitContentAcrossColumns.length; i++) {
			char letter = (char) ('a' + i - 1);
			returnString.append("<div type='textpart' subtype='column' n='" + letter + "'>");
			returnString.append(addLBTagsToContent(splitContentAcrossColumns[i].trim()));
			returnString.append("</div>");
		}
		return returnString.toString().trim();
	}

	/**
	 * Epidocifies an uncertain abbreviation
	 * @param content
	 * @return
	 */
	private static String addUnknownAbbreviationTags(String content) {
		String temp;
		Pattern pattern = Pattern.compile("([^\\s>]+)\\((\\-[ ]?){3}[ ]?\\??\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String abbr = matcher.group(1);
			content = content.replace(temp, "<abbr>" + abbr + "</abbr>");
		}
		return content;
	}

	/** 
	 * Epidocifies incomprehensible characters.
	 * Ignores Roman Numerals, but doesn't work for Greek Characters
	 */
	private static String addIncomprehensibleCharacters(String content) {
		String temp;
		Pattern pattern = Pattern.compile("([A-Z][\u0332,\u0323,\u0302]?){2,}");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			if (!temp.matches("[C,D,I,L,M,V,X]+")) {
				content = content.replace(temp, "<orig>" + temp.toLowerCase() + "</orig>");
			}
		}
		return content;
	}

	/**
	 * Epidocify '<:textus non legitur>'
	 * 
	 * @param content
	 * @return
	 */
	private static String addIllegibleText(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:textus non legitur\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<gap reason='illegible' extent='unknown' unit='character'/>");

		}
		return content;
	}

	/**
	 * Epidocify '<:vacat>'
	 * 
	 * @param content
	 * @return
	 */
	private static String addBlankSpace(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:vacat\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<space/>");

		}
		return content;
	}

	/**
	 * Epidocify '<:ianua>'
	 * 
	 * @param content
	 * @return
	 */
	private static String addDoorSpace(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:ianua\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<space type='door'/>");
		}
		return content;
	}

	/**
	 * Epidocify all other forms of '<:[something]>' as subaudible
	 * 
	 * @param content
	 * @return
	 */
	private static String addSubaudible(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\&\\#60\\;\\:([^\\s]+)\\&\\#62\\;");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<supplied reason='subaudible'>" + matcher.group(1) + "</supplied>");
		}
		return content;
	}

	/**
	 * Epidocify conventions of the form ([some content]) as subaudible
	 * 
	 * @param content
	 * @return
	 */
	private static String addSubAudibleSymbols(String content) {
		String temp;
		Pattern pattern = Pattern.compile("(?<= )\\(([^\\s:]+)\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			content = content.replace(temp, "<supplied reason='subaudible'>" + matcher.group(1) + "</supplied>");
		}
		return content;
	}

	/**
	 * Epidocify non-standard symbols
	 * 
	 * @param content
	 * @return
	 */
	private static String addSymbols(String content) {
		String temp;
		Pattern pattern = Pattern.compile("\\(\\([^\\(\\)\\:]+\\)\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			temp = matcher.group(0);
			String symbol = temp.replaceAll("\\(|\\)", "");
			content = content.replace(temp,
					"<expan><abbr><am><g type='" + symbol + "'/></am></abbr><ex>" + symbol + "</ex></expan>");
		}
		return content;
	}

	/**
	 * Adds the markup for word breaks across lines
	 * 
	 * @param content
	 * @return
	 */
	private static String addWordBreaks(String content) {
		Pattern pattern = Pattern.compile("[ ]?=[\\s]*\\<lb n=[\\\"']([\\d])+[\\\"']/\\>");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String temp = matcher.group(0);
			String count = matcher.group(1);
			content = content.replace(temp, "<lb n=\"" + count + "\" break=\"no\"/>");
		}
		return content;
	}

	/**
	 * Add mark up for lines that were completely lost when the extent of the damage is unknown
	 * 
	 * @param content
	 * @return
	 */
	private static String addLostLinesExtentKnown(String content) {
		// Must be done before we determine line breaks
		Pattern pattern = Pattern.compile("\\[([ ]?-[ ]?){6}\\](\\n\\[([ ]?-[ ]?){6}\\])*");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String temp = matcher.group(0);
			Matcher nestedMatcher = Pattern.compile("\\[([ ]?-[ ]?){6}\\]").matcher(temp);
			int quant = 0;
			while (nestedMatcher.find()) {
				quant += 1;
			}
			content = content.replace(temp, "<gap reason='lost' extent='" + quant + "' unit='line'/>");
		}
		return content;
	}

	/**
	 * A massive method that epidocifies all content of the form [] This allows for
	 * more than one type of convention to be used within the brackets Example:
	 * [---accep] or [---d]ece[mbr?---]
	 * 
	 * @param content
	 * @return
	 */
	private static String addSquareBrackets(String content) {
		String original, match, temp, quant;
		Pattern pattern = Pattern.compile("\\[[^\\[\\]]+\\]");
		Matcher matcher = pattern.matcher(content);
		Matcher nestedMatcher;
		while (matcher.find()) {
			original = matcher.group(0);
			match = original.replaceAll("\\[|\\]", "");
			// nestedMatcher = Pattern.compile("((\\-\\-
			// \\-|\\-{3})|([^\\-])+)").matcher(match);
			nestedMatcher = Pattern.compile("(((\\-([ ]\\-)*)+)|(\\+([0-9]+)\\?\\+)|(([^\\-])+)|(•([ ]•)*)+)")
					.matcher(match);
			while (nestedMatcher.find()) {
				temp = nestedMatcher.group(0);
				if (temp.equals("---") || temp.equals("- - -")) {
					match = match.replaceFirst(temp, "<gap reason='lost' extent='unknown' unit='character'/>");
				}
				// Dot character to replace '-' with '•' once the classicists are ready
				else if (temp.matches("((•[ ]?)+)")) {
					quant = ((Integer) (temp.replaceAll(" ", "").length())).toString();
					match = match.replaceFirst(temp, "<gap reason='lost' quantity='" + quant + "' unit='character'/>");
				} else if (temp.matches("((\\-[ ]?)+)")) {
					quant = ((Integer) (temp.replaceAll(" ", "").length())).toString();
					match = match.replaceFirst(temp, "<gap reason='lost' quantity='" + quant + "' unit='character'/>");
				} else if (temp.matches("(\\+[0-9]+\\?\\+)")) {
					quant = nestedMatcher.group(6); // This may change if regular express is changed
					match = match.replace(temp,
							"<gap reason='lost' quantity='" + quant + "' unit='character' precision='low'/>");
				} else {
					if (temp.matches("[^\\?]+\\?[ ]?")) {
						match = match.replace(temp,
								"<supplied reason='lost' cert='low'>" + temp.replaceAll("\\?", "") + "</supplied>");
					} else {
						match = match.replace(temp, "<supplied reason='lost'>" + temp + "</supplied>");
					}
				}
			}
			content = content.replace(original, match);
		}

		return content;
	}

	/**
	 * Translates Unicode characters to combinational characters
	 */
	private static void buildMap() {
		// Translate Sublinear Dots
		String[] unicodeChars = { "Ạ", "Ḅ", "Ḍ", "Ẹ", "Ḥ", "Ị", "Ḳ", "Ḷ", "Ṃ", "Ṇ", "Ọ", "Ṛ", "Ṣ", "Ṭ", "Ụ", "Ṿ", "Ẉ",
				"Ỵ", "Ẓ", "ạ", "ḅ", "ḍ", "ẹ", "ḥ", "ị", "ḳ", "ḷ", "ṃ", "ṇ", "ọ", "ṛ", "ṣ", "ṭ", "ụ", "ṿ", "ẉ", "ỵ",
				"ẓ", };
		String[] associatedLetters = { "A", "B", "D", "E", "H", "I", "K", "L", "M", "N", "O", "R", "S", "T", "U", "V",
				"W", "Y", "Z", "a", "b", "d", "e", "h", "i", "k", "l", "m", "n", "o", "r", "s", "t", "u", "v", "w", "y",
				"z" };
		for (int i = 0; i < unicodeChars.length; i++) {
			charMap.put(unicodeChars[i], associatedLetters[i] + "\u0323");
		}
		// Translate Carots
		String[] unicodeChars_carots = { "Â", "â", "Ĉ", "ĉ", "Ê", "ê", "Ĝ", "ĝ", "Ĥ", "ĥ", "Î", "î", "Ĵ", "ĵ", "Ô", "ô",
				"Ŝ", "ŝ", "Û", "û", "Ŵ", "ŵ", "X̂", "x̂", "Ŷ", "ŷ", "Ẑ", "ẑ" };
		String[] associatedLetters_carots = { "A", "a", "C", "c", "E", "e", "G", "g", "H", "h", "I", "i", "J", "j", "O",
				"o", "S", "s", "U", "u", "W", "w", "X", "x", "Y", "y", "Z", "z" };
		for (int i = 0; i < unicodeChars_carots.length; i++) {
			charMap.put(unicodeChars_carots[i], associatedLetters_carots[i] + "\u0302");
		}
	}

	/**
	 * Convert all unicode characters in the content to combinational characters 
	 * 
	 * @param str
	 * @return
	 */
	private static String translateUnicode(String str) {
		// Translate Dots and Carots
		String returnStr = "";
		for (int i = 0; i < str.length(); i++) {
			String temp = Character.toString(str.charAt(i));
			if (charMap.containsKey(temp)) {
				returnStr += charMap.get(temp);
			} else {
				returnStr += str.charAt(i);
			}
		}
		return returnStr;
	}

	/**
	 * Convert HTML encoding into plain strings for processing and run translateDots
	 * 
	 * Replaces both two types of angle brackets with the unicode for < and >. This is
	 * as a safeguard against incorrect entries. So, to find graffiti containing
	 * the〈: 〉, need to search for &#60; and &#62; 
	 * This replacement ensures all entries of this type are flagged
	 * @param content
	 * @return
	 */
	public static String normalize(String content) {
		if (content != null) {
			return translateUnicode(StringEscapeUtils.unescapeHtml4(content)).replaceAll("<|〈", "&#60;")
					.replaceAll(">|〉", "&#62;");
		}
		return "";

	}

	

}
