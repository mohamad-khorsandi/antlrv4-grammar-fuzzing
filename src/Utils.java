import java.util.List;
import java.util.Random;

public class Utils {
    public static <T> T randomElem(List<T> l) {
        return l.get(new Random().nextInt(l.size()));
    }
}
