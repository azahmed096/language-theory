import java.util.List;

/**
 * Class that contains implementations of
 * some static functions
 * that are not available in java 6 but in java 8
 */
public class Java8util{
    /**
     * replace String.join
     */
    public static String Stringjoin(String delimiter,  List objects){
        if (objects.size() == 0) return "";
        
        StringBuilder builder = new StringBuilder();

        builder.append(objects.get(0));
        int const_size = objects.size();
        for (int i = 1; i < const_size; ++i){
            builder.append(delimiter);
            builder.append(objects.get(i));
        }
        return builder.toString();
    }
}