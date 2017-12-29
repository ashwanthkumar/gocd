package in.ashwanthkumar.gocd.database;

public class SqlUtil {
    // Inspired from https://github.com/gocd/gocd/blob/master/server/src/com/thoughtworks/go/server/util/SqlUtil.java
    public static <T> String joinWithQuotesForSql(T[] array) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            T t = array[i];
            builder.append(escape(t.toString()));
            if (i < array.length - 1) {
                builder.append(',');
            }
        }
        return builder.toString();
    }

    public static String escape(String value) {
        return "QUOTE(" + value + ")";
    }

}
