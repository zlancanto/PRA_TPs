package mihan.sossou.tp5;

public enum Owner {
	NONE, FIRST, SECOND;

	public Owner opposite() {
		return this == SECOND ? FIRST : this == FIRST ? SECOND : NONE;
	}
}