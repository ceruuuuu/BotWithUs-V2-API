package net.botwithus.rs3.minimenu;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public interface Interactable {

    List<String> getOptions();

    boolean interact(int index);

    default boolean interact() {
        return interact(0);
    }

    default boolean interact(Predicate<String> predicate) {
        List<String> options = getOptions();
        for (int i = 0; i < options.size(); i++) {
            String opt = options.get(i);
            if (opt == null) {
                continue;
            }
            if (predicate.test(opt)) {
                return interact(i);
            }
        }
        return false;
    }

    default boolean interact(String option) {
        return interact(opt -> opt.equals(option));
    }

    default boolean interact(Pattern pattern) {
        return interact(opt -> pattern.matcher(opt).matches());
    }
}