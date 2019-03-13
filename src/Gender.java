public enum Gender {
    BLANK(""),
    M("M"),
    F("F"),;

    private final String name;

    Gender(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }


}
