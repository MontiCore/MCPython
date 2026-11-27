# Currently known to be unsupported

## Tuples without parentheses
The usage of tuples that are not parenthesized is not supported as these causes issues with antlr. (See https://www.geeksforgeeks.org/python/when-are-parentheses-required-around-a-tuple-in-python/ for unparenthesized tuples)
 We define the rules:
 
 ```TupleLiteral implements Literal = "(" (VariableInit || ",")* ","? ")" ;```
 ```SimpleInit implements VariableInit = Expression ;```
 and ExpressionBasis.mc4 ```LiteralExpression implements Expression <340> = Literal;```
 
 Thereby without the parentheses we would cause left recursion which is also reachable by ``LiteralStatement implements ClassStatement = Literal STATEMENT_END;```.
 
## Unicode names
 Further for now only names with latin letters are permitted, in python another chars are allowed as well, see https://docs.python.org/3/reference/lexical_analysis.html#identifiers. 
 Prototyping with unicode names have passed the parser tests, further testing needs to be done.
 
```  @Override
 token Name =
            ( UnicodeChar | '_' | '$' )
            ( UnicodeChar | '_' | '0'..'9' | '$' )*;
 // Latin,Greek,Coptic,Cyrillic,Armenian
 fragment token UnicodeChar = 'a'..'z'
                                  |'A'..'Z'
                                  |'\u00C0'..'\u00D6'
                                  |'\u00D8'..'\u00F6'
                                  |'\u00F8'..'\u02AF'
                                  |'\u0370'..'\u0373'
                                  |'\u0376' | '\u0377' | '\u037F' | '\u0386'
                                  |'\u0386'..'\u03E1'
                                  |'\u03E2'..'\u0481'
                                  |'\u048A'..'\u0588';
```
