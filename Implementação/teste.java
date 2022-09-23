import java.util.ArrayList;

public class teste {
    public static void main(String[] args) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ids.add(3);
        ids.add(2);
        ids.add(1);
        System.out.println(ids);
        ids.sort(null);
        System.out.println(ids);
    }
}
