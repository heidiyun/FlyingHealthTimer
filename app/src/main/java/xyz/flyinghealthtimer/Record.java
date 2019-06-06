package xyz.flyinghealthtimer;

public class Record {
    private String name;
    private int count;

    public Record(String name, int count) {

        this.name = name;
        this.count = count;

    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
