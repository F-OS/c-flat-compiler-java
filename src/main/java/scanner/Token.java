/*
 * Copyright (c) 2023.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of  MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package scanner;

public class Token {
	private final int line;
	private final int charnum;

	public Token(int line, int character) {
		this.line = line;
		charnum = character;
	}

	public Token() {
		line = -1;
		charnum = -1;
	}

	public Location getTokenLoc() {
		return new Location(line, charnum);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		Token other = (Token) obj;
		return line == other.line && charnum == other.charnum;
	}

	@Override
	public String toString() {
		return "Token{" +
					   "line=" + line +
					   ", charnum=" + charnum +
					   '}';
	}

	public record Location(int line, int character) {
	}
}