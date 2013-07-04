/*******************************************************************************
 * This file is part of RedReader.
 *
 * RedReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RedReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RedReader.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.quantumbadger.redreader.reddit.prepared;

import java.util.Arrays;

public final class MarkdownTokenizer {

	public static final int
			TOKEN_UNDERSCORE = -1,
			TOKEN_UNDERSCORE_DOUBLE = -2,
			TOKEN_ASTERISK = -3,
			TOKEN_ASTERISK_DOUBLE = -4,
			TOKEN_TILDE_DOUBLE = -5,
			TOKEN_CARET = -6,
			TOKEN_GRAVE = -7,
			TOKEN_BRACKET_SQUARE_OPEN = -8,
			TOKEN_BRACKET_SQUARE_CLOSE = -9,
			TOKEN_PAREN_OPEN = -10,
			TOKEN_PAREN_CLOSE = -11;

	
	public static int[] tokenizeAndClean(final char[] rawArr) {

		final int[] result = tokenize(rawArr);
		final boolean[] toRevert = new boolean[result.length];

		int lastUnderscore = -1, lastUnderscoreDouble = -1;
		int lastAsterisk = -1, lastAsteriskDouble = -1;
		int lastTildeDouble = -1;
		int lastGrave = -1;

		int lastBracketSquareOpen = -1;

		for(int i = 0; i < result.length; i++) {

			final int c = result[i];

			switch(c) {

				case TOKEN_UNDERSCORE:
					lastUnderscore = lastUnderscore < 0 ? i : -1;
					break;

				case TOKEN_UNDERSCORE_DOUBLE:

					if(lastUnderscoreDouble < 0) {
						lastUnderscoreDouble = i;

					} else {

						if(lastUnderscoreDouble == i - 1) {
							toRevert[lastUnderscoreDouble] = true;
							toRevert[i] = true;
						}

						lastUnderscoreDouble = -1;
					}

					break;

				case TOKEN_ASTERISK:
					lastAsterisk = lastAsterisk < 0 ? i : -1;
					break;

				case TOKEN_ASTERISK_DOUBLE:

					if(lastAsteriskDouble < 0) {
						lastAsteriskDouble = i;

					} else {

						if(lastAsteriskDouble == i - 1) {
							toRevert[lastAsteriskDouble] = true;
							toRevert[i] = true;
						}

						lastAsteriskDouble = -1;
					}

					break;

				case TOKEN_TILDE_DOUBLE:

					if(lastTildeDouble < 0) {
						lastTildeDouble = i;

					} else {

						if(lastTildeDouble == i - 1) {
							toRevert[lastTildeDouble] = true;
							toRevert[i] = true;
						}

						lastTildeDouble = -1;
					}

					break;

				case TOKEN_GRAVE:

					if(lastGrave < 0) {
						lastGrave = i;

					} else {

						if(lastGrave == i - 1) {
							toRevert[lastGrave] = true;
							toRevert[i] = true;
						}

						lastGrave = -1;
					}

					break;

				case TOKEN_BRACKET_SQUARE_OPEN:
					if(lastBracketSquareOpen < 0) {
						lastBracketSquareOpen = i;
					} else {
						result[lastBracketSquareOpen] = '[';
						lastBracketSquareOpen = i;
					}
					break;

				case TOKEN_BRACKET_SQUARE_CLOSE:
					if(lastBracketSquareOpen < 0) {
						result[i] = ']';
					} else {
						// TODO check link
					}
					break;

				case TOKEN_PAREN_OPEN:
				case TOKEN_PAREN_CLOSE:
					toRevert[i] = true;
					break;
			}
		}

		if(lastUnderscore >= 0) toRevert[lastUnderscore] = true;
		if(lastUnderscoreDouble >= 0) toRevert[lastUnderscoreDouble] = true;
		if(lastAsterisk >= 0) toRevert[lastAsterisk] = true;
		if(lastAsteriskDouble >= 0) toRevert[lastAsteriskDouble] = true;
		if(lastTildeDouble >= 0) toRevert[lastTildeDouble] = true;
		if(lastGrave >= 0) toRevert[lastGrave] = true;
		if(lastBracketSquareOpen >= 0) toRevert[lastBracketSquareOpen] = true;

		// TODO revert into new array

		return result;
	}

	// TODO inline, use resultPos instead of copyOf
	private static int[] tokenize(final char[] rawArr) {

		final int[] result = new int[rawArr.length];
		int resultPos = 0;

		for(int i = 0; i < rawArr.length; i++) {

			final char c = rawArr[i];

			switch(c) {

				case '*':

					if(i < rawArr.length - 1 && rawArr[i + 1] == '*') {
						i++;
						result[resultPos++] = TOKEN_ASTERISK_DOUBLE;
					} else {
						result[resultPos++] = TOKEN_ASTERISK;
					}

					break;

				case '_':

					// TODO check previous
					if(i < rawArr.length - 1 && rawArr[i + 1] == '_') {
						i++;
						result[resultPos++] = TOKEN_UNDERSCORE_DOUBLE;
					} else {
						result[resultPos++] = TOKEN_UNDERSCORE;
					}

					break;

				case '~':

					if(i < rawArr.length - 1 && rawArr[i + 1] == '~') {
						i++;
						result[resultPos++] = TOKEN_TILDE_DOUBLE;

					} else result[resultPos++] = '~';

					break;

				case '`':
					result[resultPos++] = TOKEN_GRAVE;
					break;

				case '[':
					result[resultPos++] = TOKEN_BRACKET_SQUARE_OPEN;
					break;

				case ']':
					result[resultPos++] = TOKEN_BRACKET_SQUARE_CLOSE;
					break;

				case '(':
					result[resultPos++] = TOKEN_PAREN_OPEN;
					break;

				case ')':
					result[resultPos++] = TOKEN_PAREN_CLOSE;
					break;

				case '\\':
					if(i < rawArr.length - 1) result[resultPos++] = rawArr[++i];
					else result[resultPos++] = '\\';
					break;

				default:
					result[resultPos++] = c;
					break;
			}
		}

		return Arrays.copyOf(result, resultPos);
	}
}