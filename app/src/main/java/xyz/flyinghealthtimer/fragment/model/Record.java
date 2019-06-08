package xyz.flyinghealthtimer.fragment.model;

public class Record {
    private String name;
    private int count;
    private int round;

    public Record(String name, int count, int round) {

        this.name = name;
        this.count = count;
        this.round = round;

    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public int getRound() {
        return round;
    }
}
