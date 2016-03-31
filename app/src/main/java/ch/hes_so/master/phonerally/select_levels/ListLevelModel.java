package ch.hes_so.master.phonerally.select_levels;

public class ListLevelModel {
    private String name;
    private String resourceId;

    public ListLevelModel(String name, String resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
