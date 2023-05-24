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

public abstract class Token {
	protected final int line;
	protected final int charnum;

	@Override
	public abstract String toString();

	protected Token(int curline, int curcharacter) {
		line = curline;
		charnum = curcharacter;
	}

	protected Token() {
		line = -1;
		charnum = -1;
	}

	public final Location getTokenLoc() {
		return new Location(line, charnum);
	}

	public record Location(int line, int character) {
	}
}