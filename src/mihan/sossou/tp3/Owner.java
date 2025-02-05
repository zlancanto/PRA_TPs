package mihan.sossou.tp3;

public enum Owner
{
    NONE, FIRST, SECOND;

    public Owner opposite() {
        switch (this) {
            case SECOND:
                return FIRST;
            case FIRST:
                return SECOND;
            default:
                return NONE;
        }
    }
}