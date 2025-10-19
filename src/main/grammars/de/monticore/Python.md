<!-- (c) https://github.com/MontiCore/monticore -->
<!-- This is a MontiCore alpha explanation, with plans to become MontiCore stable. -->

# Python
The Python language defines an almost complete subset of the Python programming language.  
The goal is to analyze existing Python (research) software; therefore, the grammar and CoCos are somewhat simplified.  
Because the parsed code is assumed to be valid Python, as verified by other tools, certain restrictions can be omitted, e.g., accepting code that is not strictly valid Python.  
The focus is instead on building an abstract syntax that is easy to analyze within the MontiCore toolchain.

## Whitespace sensitive parsing
Since python is a whitespace sensitive language, some special adaptions to the language frontend have to be made.
The lexer emits special tokens to control the increase and decrease of indent, as well as the end of statements.
Thus, the parser can be written identically to a c-style language, using these newly introduced tokens instead of curly brackets and semicolons.
The whole process is described in detail in "Building Whitespace-Sensitive Languages Using Whitespace-Insensitive Components".

## Example
Two simple method definitions and a method call in Python:

```python
def func1():
     v1 = func2(5)
     v2 = func2(v1)
     return func2(v2)/1

def func2(x):
     return x / 2

func1()
```

### Functionality (Cocos)
To ensure correct functionality, several requirements were implemented in the form of CoCos.

#### 1. CallExpressionAfterFunctionDeclarationCoco
This Coco checks whether a function is called after being declared. It compares the positions of the call and the declaration.  
Functions imported from other files are excluded from this check, as their position is no longer relevant.

#### 2. JavaBooleanExpressionCoco
This Coco blocks all Java-style boolean operators. Symbols such as `&&` and `||`, which represent the logical operators `and` and `or` in Java, cannot be used in Python.  
This Coco blocks such expressions and returns the error `InvalidBooleanExpression`, along with the symbol and its location.

#### 3. PythonDuplicateFunctionCoco
This Coco detects whether functions are defined more than once in a Python file.  
If duplicate function names are found, it returns an error. Function overloading is not supported in this project, as explained in the Evaluation Section.

#### 4. PythonExpressionCoco
This Coco checks all expressions that stand alone on a line to determine whether they are call or assignment expressions.  
If the expression is neither, it returns a warning for having no effect. If an invalid assignment is found (e.g., without a proper variable declaration), it returns an error.  
This prevents unnecessary statements with no purpose.

#### 5. PythonFunctionArgumentSizeCoco
This Coco verifies that a function is called with the correct number of arguments.  
It ensures that the number of arguments provided matches the minimum and maximum defined in the function declaration, including optional parameters.

#### 6. PythonFunctionOrClassDeclarationInStatementBlockCoco
This Coco prohibits declaring functions or classes inside control structures such as `if`, `for`, or `while` blocks.  
An error is returned if this rule is violated.

#### 7. PythonFunctionDuplicateParameterNameCoco
This Coco checks for duplicate parameter names in a function definition.  
If two parameters share the same name, an error is returned, indicating a duplicate parameter name and its position.

#### 8. PythonLambdaDuplicateParameterNameCoco
This Coco performs the same check as the previous one but for lambda expressions.  
If duplicate parameter names are found, an error is returned along with the position of the issue.

#### 9. PythonVariableOrFunctionOrClassExistsCoco
This Coco verifies that any variable, function, or class being used actually exists.  
It checks the symbol table to confirm whether the referenced symbol has already been defined.

### Symbol Table
The Symbol Table stores all used symbols, such as variables, classes, and functions.  
When a symbol is reused, its reference is linked to the already created symbol for consistent usage.  
The table is also used to verify that symbol names do not match Python keywords, thereby preventing invalid identifiers.  
Additionally, function symbols are stored to enable validation of correct symbol usage.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

