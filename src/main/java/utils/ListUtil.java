package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import static main.SingletonInjector.rand;


public class ListUtil<T> extends ArrayList<T> {
    protected ListUtil(Collection<? extends T> c) {
        super(c);
    }

    public ListUtil() {
    }


    public static <V> ListUtil<V> by(Collection<? extends V> c, Predicate<V> filter) {
        ListUtil<V> res = new ListUtil<>();
        c.stream().filter(filter).forEach(res::add);
        return res;
    }


    public static <V> ListUtil<V> by(Collection<V> c) {
        return new ListUtil<>(c);
    }

    public T randElem() {
        return this.get(rand.nextInt(this.size()));
    }

    public static <S> S randElem(List<S> list) {
        return list.get(rand.nextInt(list.size()));
    }
}
