package jmind.pigg;

import java.util.regex.Pattern;

import jmind.pigg.binding.DefaultInvocationContext;
import jmind.pigg.binding.InvocationContext;
import jmind.pigg.parser.ASTRootNode;
import jmind.pigg.parser.generate.ParseException;
import jmind.pigg.parser.generate.Parser;
import jmind.pigg.util.logging.InternalLogLevel;

public class Test {
   static Pattern ASTJDBCIterableParameter = Pattern.compile("in\\s*\\(\\s*\\$(\\w+)((\\.\\w+)*)(\\@\\w+)?\\s*\\)", Pattern.CASE_INSENSITIVE);
   static Pattern ASTJDBCParameter = Pattern.compile(":(\\w+)((\\.\\w+)*)(\\@\\w+)?");
   static  Pattern ASTJoinParameter = Pattern.compile("#\\{\\s*(:(\\w+)(\\.\\w+)*)\\s*\\}", Pattern.CASE_INSENSITIVE);


    public static void main(String[] args) {

        InternalLogLevel level=InternalLogLevel.DEBUG;
        System.out.println(InternalLogLevel.TRACE.compareTo(level));
        System.out.println(InternalLogLevel.DEBUG.compareTo(level));
        System.out.println(InternalLogLevel.INFO.compareTo(level));
        System.out.println(InternalLogLevel.ERROR.compareTo(level));
       for(InternalLogLevel level1: InternalLogLevel.values()){
           System.out.println(level1.ordinal());
       }
    }

}
