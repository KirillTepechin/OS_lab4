import java.util.LinkedList;

public class File {
    String name;
    LinkedList<Integer> list = new LinkedList<>();

    /**
     * Переопредяляем для возможности задания названия файла, при этом привязав node к file
     *
     * @return название файла
     */
    @Override
    public String toString() {
        return name;
    }

}
