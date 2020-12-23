package model;

public class Headline {
    private int id;
    private String name;

    /**
     * @param id   the position
     * @param name the headline
     */
    public Headline(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /**
     * @return the position
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the position to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the headline
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the headline to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
