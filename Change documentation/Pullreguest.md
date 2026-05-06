# Pullrequest to incorporate new Python functionality.
In this Markdown all changes will be presented and reasoned.
## Grammar

### Rework of For Decomposition
Python currently allows another targets inside the for constructs, thereby we change our ForControl non terminal to be closer to the python language.

Old: 
```
 ForControl = ForDecomposition "in" ForIterable;
 interface ForDecomposition;
 ForDecompositionComma implements ForDecomposition = ForDecomposition "," (ForDecomposition ","?)?;
 ForDecompositionParenthesis implements ForDecomposition = "("ForDecomposition ")";
 ForDecompositionBrackets implements ForDecomposition = "[" ForDecomposition "]";
 ForStarredVariable  implements ForDecomposition = "*" ForDecomposition;
```
New:
```
 ForControl = ForList "in" ForIterable;
 interface ForDecomposition;
 ForList = (ForDecomposition || ",")+ ","?;
 ForDecompositionParenthesis implements ForDecomposition = "(" ForList? ")";
 ForDecompositionBrackets implements ForDecomposition = "[" ForList? "]";
 ForStarredVariable  implements ForDecomposition = "*" ForDecomposition ;
 ForPyQualifiedName implements ForDecomposition = PyQualifiedName;
```
Python: ("[...]" Marks optional)
```
comprehension: assignment_expression comp_for
comp_for:      ["async"] "for" target_list "in" or_test [comp_iter]
target_list:     target ("," target)* [","]
target:          identifier
                 | "(" [target_list] ")"
                 | "[" [target_list] "]"
                 | attributeref
                 | subscription
                 | "*" target

```
 Sources: https://docs.python.org/3/reference/simple_stmts.html#grammar-token-python-grammar-target_list and https://docs.python.org/3/reference/expressions.html (6.2.5)

### Rework of Boolean
Instead of Defining a new BooleanLiteralPython, we override the Monticore BooleanLiteral to allow 'true' and 'false' as names, tests have shown that they can be used as such.

### Rework of fstrings
It proved to be difficult to support all variants of fStrings, so far they were parsed as a singular string token, thereby the content of curly brackets was considered text, yet tests have shown that line breaks are allowed within curly brackets. Another issue is the allowed usage of the same quotes " or ' in the outer string as well as inside the curly brackets. Finally it is possible to add f to chars in python such as ```f'a'``` this was so far an issue as only strings were allowed to be modified and not chars.
With the following changes we aim combat these issues.

1) We don't allow f and F as string modifier (line 121)
2) We add chars to the string definitions (line 115,line 120)
3) We add new tokens for fstrings
   ```
   //Double and single quoted strings with an f modifier f'text1{exp}text2', separate definition to allow more expressions.
        token FSQStringPython
          = ('f'|'F') '\'' (StringFSQCharactersPython)? '\'' : {setText(getText().substring(1, getText().length() - 1));};
        token FDQStringPython
          = ('f'|'F') '"' (StringFDQCharactersPython)? '"' : {setText(getText().substring(1, getText().length() - 1));};
        fragment token StringFSQCharactersPython = (StringFSQCharacterPython)+;
        fragment token StringFDQCharactersPython = (StringFDQCharacterPython)+;
        fragment token StringFSQCharacterPython = ~ ('\''| '\\'| '{' )| PythonEscapeSequence |InnerFString;
        fragment token StringFDQCharacterPython = ~ ('"'| '\\'| '{') | PythonEscapeSequence |InnerFString;
        fragment token InnerFString = '{' ~('}')* '}'; 
    ```
4) We similarly define fstring literals and non terminals for multilinestring tokens
5) We add a new FStringPython non terminal (line 120) and add it to the StringLiteralPython (line 112-117)


### Addition of complex numbers.
Python supports the usage of complex numbers, they are formatted as:
  (real number)? (+|-)? (imaginaryNumber)'j'
A token PyComplexNumber was added supporting these numbers, additional a matching literal for their usage within python code.
``` token PyComplexNumber = (DigitsPart | PyFloat)? ('+' | '-')? DigitsPart "j";
    PyComplexNumberLiteral implements NumericLiteral <95> = PyComplexNumber;
```
Source for cmath: https://docs.python.org/3/library/cmath.html 
### Addition of generics.

We added  support for generics as follows:
```  
  GenericsAnnotation = "[" Generics? "]";
  Generics = (Generic || ",")+ ;
  Generic = TypeAnnotation (":" TypeAnnotation)?;
```
This is done similarly to https://docs.python.org/3/reference/compound_stmts.html#type-params.
But differently to use the existing TypeAnnotation non terminal. To support the functionality described in the source above we added "GenericsAnnotation?" to:
 - The the non terminals implementting the interface FunctionParameter.
 - ClassFunctionDeclaration,ClassDeclaration,SimpleFunctionDeclaration,TypeDeclarationStatement   
 and the TypeAnnotations  ``` GenericTypeAnnotation implements TypeAnnotation = TypeAnnotation GenericsAnnotation```

### Statements

### ClassStatements
Import, If, Assert, For, While,  ConditionalExecution,  With, GlobalVariableDeclaration, NonLocalVariableDeclaration, MultiVariableDeclaration, ParenMultiVariableDeclaration, Raise, TypeRuleStatement,Delete statements are now implementing the ClassStatement interface. By experimenting and testing they showed to be allowed inside classes.

#### New Statements

##### Nonlocal
For a detailed description of the nonlocal keyword see: https://docs.python.org/3/tutorial/classes.html under 9.2 . Summarizing the source, nonlocal allows a function or class defined inside another function to accesses the enclosing functions variables. Without the non local keyword these variables are read only to nested functions and classes. Our implementation is similar to pythons https://docs.python.org/3/reference/simple_stmts.html#nonlocal.

Pythons: ```nonlocal_stmt: "nonlocal" identifier ("," identifier)* ```(see source above)
Ours : ```NonLocalVariableDeclaration implements Statement,ClassStatement = "nonlocal" names:(Name || ",")+ STATEMENT_END;```

##### Yield from
Yield from statements in the form: 'yield' 'from' expression have been added in https://peps.python.org/pep-0380/ ,we support them similarly to python https://docs.python.org/3/reference/expressions.html#grammar-token-python-grammar-yield_expression.

Pythons: ```yield_from: "yield" "from" expression``` (see source above)
Ours:```YieldFromStatement implements Statement = "yield" "from" Expression STATEMENT_END; ```


##### Type Declaration Statement
To allow type hints as ``` var : int``` without an assignment (in comparison to augmented assignments), we add the new TypeDeclartationStatement.
``` 
TypeDeclaration implements Statement = Expression ":" TypeAnnotation STATEMENT_END;
```
Python incorporates this functionality in the augmented assignments, yet this caused issues for us. see https://docs.python.org/3/reference/simple_stmts.html#index-15.

##### Type Aliases
Python allows to declare type aliases. Where a type is aliased under an identifier, see https://docs.python.org/3/reference/simple_stmts.html#grammar-token-python-grammar-type_stmt.

We implement the support similarly, also we define the type alias as symbol.
Python: ```type_stmt: 'type' identifier [type_params] "=" expression``` (see source above)
Ours: ```symbol TypeRuleStatement implements Statement,ClassStatement = key("type") Name GenericsAnnotation?  "=" Expression STATEMENT_END;```
'type' needs to be declared as local keyword as tests have shown that it can be used as variable name. Python also specifies this https://docs.python.org/3/reference/lexical_analysis.html#soft-keywords .

#### Changes

##### Global
So far we only allowed a single variable to be declared as global in a global statement.
But Python allows multiple variables to be declared global in the same statement separated by commata see: https://docs.python.org/3/reference/simple_stmts.html#global , also so far we allowed a type annotation within a global statement which is not allowed, thereby this support is removed.
Additionally global can be used in classes as described in the source above. 

Old: ```GlobalVariableDeclaration implements Statement = "global" Name (":" TypeAnnotation)? STATEMENT_END;```
New:```GlobalVariableDeclaration  implements Statement,ClassStatement = "global" (Name || ",")+ STATEMENT_END;```

##### Class Statement Block 
A ClassStatementBlock is now allowed to be a singular ClassStatement to allow classes such as:
```
class name1: pass
class name2: a=1
```
Old: ```ClassStatementBlock = BLOCK_START ClassStatementBlockBody BLOCK_END;```
New: ```ClassStatementBlock = (BLOCK_START ClassStatementBlockBody BLOCK_END)|ClassStatement;```

Test have shown that this is needed.

#### Match Statement
Case statements in python allow patterns to be aliased (as example ```case Type as t:```) see https://docs.python.org/3/reference/compound_stmts.html#the-match-statement and https://docs.python.org/3/reference/compound_stmts.html#grammar-token-python-grammar-patterns. To allow this, case statements now span a scope to capture the alias.
Old:
```
MatchStatement implements Statement = key("match") Expression ":" MatchBlock;
scope MatchBlock = BLOCK_START CaseStatement* BLOCK_END;
CaseStatement = key("case") (Expression || "|")+ ("if" condition:Expression)? ":" StatementBlock;
```
New:
```
MatchStatement implements Statement,ClassStatement = key("match") Expression ":" MatchBlock;
scope MatchBlock = BLOCK_START CaseStatement* BLOCK_END;
scope CaseStatement = key("case") (Expression || "|")+ ("as" Alias)? ("if" condition:Expression)? ":" CaseStatementBlock;
CaseStatementBlock = BLOCK_START Statement* BLOCK_END | Statement;
```
(Alias is defined as ```symbol Alias = Name;````)
Aditionally we allowed match statements inside classes.

#### Try-Except
PEP758 Added support for leaving out parenthesis. Further so far the support for various Expressions and starred expressions were missing as specified in https://peps.python.org/pep-0758/ we added the support for this accordingly as specified. Also similarly to the new case statements, excepts now span a scope with the alias non terminal. 
Old:
```
 ExceptStatement = "except" (PyQualifiedName? | "(" (PyQualifiedName || ",")+ ")") ("as" alias:Name)? ":" StatementBlock;

```
New: 
```
scope ExceptStatement = "except" ExceptPattern?  ":" ExceptStatementBlock;
ExceptStatementBlock =  BLOCK_START Statement* BLOCK_END | Statement;
//PEP758
interface ExceptPattern;
ExpressionListing implements ExceptPattern = (Expression || ",")+;
ParenthesisedExpressionListing implements  ExceptPattern = "(" (Expression || ",")+ ")" ("as" Alias)?;
StarredExpressionListing implements ExceptPattern = "*"(Expression || ",")+;
StarredParenthesisedExpressionListing implements  ExceptPattern = "*""(" (Expression || ",")+ ")" ("as" Alias)?;
```
### Expressions

#### Changes

##### Assignment
As defined by https://docs.python.org/3/reference/simple_stmts.html#index-14 python's assignment statements additionally supports "//=" "@=" "**=" thereby we add this support by overriding the AssignmentExpression from Monticore by simply adding the three additional operators to the square brackets.
```
 @Override
    AssignmentExpression implements Expression <60> = <rightassoc>
        left:Expression
        operator: [ "=" | "+=" | "-=" | "*=" | "/=" | "&=" | "|="
                  | "^=" | ">>=" | ">>>=" | "<<=" | "%=" | "**=" | "@=" | "//="]
        right:Expression;

```
##### Augmented Assignment
So far we allowed the same operators as in the assignment expression, yet python only allows "=" see https://docs.python.org/3/reference/simple_stmts.html#index-15.

Thereby we remove the square brackets and only allow "=".
Old:
```
  AnnotatedAssignmentExpression implements Expression <60> = <rightassoc>
       left:Expression
       ":" annotated: TypeAnnotation
       operator: [ "=" | "+=" | "-=" | "*=" | "/=" | "&=" | "|="
                 | "^=" | ">>=" | ">>>=" | "<<=" | "%=" ]
       right:Expression; 
```
New:
```
   AnnotatedAssignmentExpression implements Expression <60> = <rightassoc>
       left:Expression
       ":" annotated: TypeAnnotation
       "="
       right:Expression; 
```
#### Comprehensions
We allowed multiple if inside a comprehension by changing ? to * at the Generator filters.
This is allowed by python as specified here: https://docs.python.org/3/reference/expressions.html#grammar-token-python-grammar-comp_for 

### Another
We added some trailing ","? to rules where they were noticed to be allowed while testing. We also formated the grammar and added some comments 
## Code
In this section i will add the commits instead of the code snippets for readability reasons.
### Preprocessor
Commit: https://github.com/MontiCore/MCPython/commit/8c519eff8bcdf027ae421726fc36929f0bde2dd1
In line 75-77 i added that the continue line token is always ignored to allow statements like:
```
    function(var1, var2,\
              var3, var4)
```
as these could cause issues.
### Visitor and CoCos related to aliased expressions.
To avoid aliased expressions to be used anywhere we added a matching CoCo and Visitor.
Commit: https://github.com/MontiCore/MCPython/commit/ba9e8f18fb1c038a2cca33527a1e1bb8552778ab

### Adaptation of new tests
We removed a test regarding invalid classes that are now valid (because for loops are now allowed as class statements).
We added new test cases for Python
...to do more specific...
