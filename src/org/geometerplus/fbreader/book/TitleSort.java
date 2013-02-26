package org.geometerplus.fbreader.book;

/*
 * Copyright (C) 2007-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

import java.text.Normalizer;
import java.util.*;

import org.geometerplus.fbreader.library.BookTree;

import android.annotation.TargetApi;
import android.os.Build;

public class TitleSort implements Comparator<BookTree> {
	
	@Override
	public int compare(BookTree bookTree1, BookTree bookTree2) {
		return bookTree1.Book.getSortKey().compareTo(bookTree2.Book.getSortKey());
	}
	
	private final static Map<String, String[]> ARTICLES = new HashMap<String, String[]>();
	// English articles
	private final static String[] EN_ARTICLES = new String[] {
		"the ", "a ", "an "
		};
	// French articles
	private final static String[] FR_ARTICLES = new String[] {
		"un ", "une ", "le ", "la ", "les ", "du ", "de ",
		"des ", "de la", "l ", "de l "
		};
	// German articles
	private final static String[] GE_ARTICLES = new String[] {
		"das ", "des ", "dem ", "die ", "der ", "den ",
		"ein ", "eine ", "einer ", "einem ", "einen ", "eines "
	};
	// Italian articles
	private final static String[] IT_ARTICLES = new String[] {
		"il ", "lo ", "la ", "l ", "un ", "uno ", "una ",
		"i ", "gli ", "le "
	};
	// Spanish articles
	private final static String[] SP_ARTICLES = new String[] {
		"el ", "la ", "los ", "las ", "un ", "unos ", "una ", "unas "
	};
	
	static {
		ARTICLES.put("english", EN_ARTICLES);
		ARTICLES.put("french", FR_ARTICLES);
		ARTICLES.put("german", GE_ARTICLES);
		ARTICLES.put("italian", IT_ARTICLES);
		ARTICLES.put("spanish", SP_ARTICLES);
		}

	public static String trim(String s, String language) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			s = normalize(s);
		}
		final StringBuilder buffer = new StringBuilder();
		boolean afterSpace = false;
		int start = 0;
		if(s.startsWith("M\'") || s.startsWith("Mc")) {
			buffer.append("Mac");
			start = 2;
		}
		for (int i = start; i < s.length(); i++) {
			char ch = s.charAt(i);
			// In case it is d' or l', may be it is "I'm", but it's OK.
			if (ch == '\'' || Character.isWhitespace(ch)) {
				ch = ' ';
			}

			switch (Character.getType(ch))	{
				default:
					// we do ignore all other symbols
					break;
				case Character.UPPERCASE_LETTER:
				case Character.TITLECASE_LETTER:
				case Character.OTHER_LETTER:
				case Character.MODIFIER_LETTER:
				case Character.LOWERCASE_LETTER:
				case Character.DECIMAL_DIGIT_NUMBER:
				case Character.LETTER_NUMBER:
				case Character.OTHER_NUMBER:
					buffer.append(Character.toLowerCase(ch));
					afterSpace = false;
					break;
				case Character.SPACE_SEPARATOR:
					if (!afterSpace && buffer.length() > 0) {
						buffer.append(' ');
					}
					afterSpace = true;
					break;
			}
		}

		final String result = buffer.toString();
		if (result.startsWith("a is")) {
			return result;
		}
		for (String a : ARTICLES.get(language.toLowerCase())) {
			if (result.startsWith(a)) {
				return result.substring(a.length());
			}
		}
		return result;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String normalize(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFKD);
		return s;
	}
}