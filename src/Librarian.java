public class Librarian {
    private int id;
    private String name;
    private int library;

    public Librarian(int id, int library, String name) {
        this.id = id;
        this.name = name;
        this.library = library;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLibrary(){
        return library;
    }

    @Override
    public String toString() {
        return name;
    }

}