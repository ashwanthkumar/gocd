package in.ashwanthkumar.gocd.database.mysql5.tool.rules;

import in.ashwanthkumar.utils.collections.Iterables;
import in.ashwanthkumar.utils.collections.Lists;
import in.ashwanthkumar.utils.func.Function;
import in.ashwanthkumar.utils.func.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ReservedWordWrapper implements DDLPatch {

    private final static List<String> KEYWORDS = new ArrayList<String>() {{
        add("key");
        add("value");
    }};

    private final static KeywordEscape DUMMY_ESCAPER = new KeywordEscape("KEYWORD_THAT_DOES_NOT_EXIST");

    private List<KeywordEscape> escapes = Lists.map(KEYWORDS, new Function<String, KeywordEscape>() {
        @Override
        public KeywordEscape apply(String input) {
            return new KeywordEscape(input);
        }
    });

    @Override
    public boolean shouldPatch(final String line) {
        return Iterables.exists(escapes, new Predicate<KeywordEscape>() {
            @Override
            public Boolean apply(KeywordEscape input) {
                return input.matches(line);
            }
        });
    }

    @Override
    public String patch(final String line) {
        return Lists.find(this.escapes, new Predicate<KeywordEscape>() {
            @Override
            public Boolean apply(KeywordEscape input) {
                return input.matches(line);
            }
        }).getOrElse(DUMMY_ESCAPER).escape(line);
    }

    static class KeywordEscape {
        String keyword;

        List<String> patterns;

        public KeywordEscape(final String keyword) {
            this.keyword = keyword;
            this.patterns = new ArrayList<String>() {{
                add("(" + keyword + ")");
                add(keyword + ")");
                add("(" + keyword);
                add("." + keyword);
                add(keyword + " ");
            }};
        }

        public boolean matches(final String line) {
            return Iterables.exists(this.patterns, new Predicate<String>() {
                @Override
                public Boolean apply(String input) {
                    return line.contains(input);
                }
            });
        }

        public String escape(String line) {
            return line
                    .replaceAll("\\(" + keyword + "\\)", "(`" + keyword + "`)")
                    .replaceAll(keyword + "\\)", "`" + keyword + "`)")
                    .replaceAll("\\(" + keyword, "(`" + keyword + "`")
                    .replaceAll("\\." + keyword, ".`" + keyword + "`")
                    .replaceAll( "(^\\s)*" + keyword + " ", "`" + keyword + "` ");
        }
    }
}
