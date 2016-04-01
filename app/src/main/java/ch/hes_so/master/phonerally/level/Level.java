package ch.hes_so.master.phonerally.level;

import java.util.List;

public class Level {
    private String name;
    private List<Checkpoint> checkpoints;

    public Level(String name, List<Checkpoint> checkpoints) {
        this.name = name;
        this.checkpoints = checkpoints;
    }

    public String getName() {
        return name;
    }

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

}
