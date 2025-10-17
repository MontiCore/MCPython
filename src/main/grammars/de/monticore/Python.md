# Python
The Python grammar is a limited Python-like grammar. 
Python grammar allows us to parse python scripts. This grammar supports Statements, Literals and Expressions.
The statements we define and consequently are supported by the grammar,
are the import statements, the if-else statements, the assert statements, the for while statements, the break statements, 
the try-except-finally statements, the with open statements, variable declarations and assignments including arrays
and tuples (only with brackets, more information found in the Problems section), the return statement. Functions and 
classes are also supported. We also needed to define String Literals and Boolean Literals since they were inconsistent 
to the implementation defined on MCBasics. Lastly we also define Expressions, it supports the ternary-operator
expression, mathematical and logical expressions and finally also lambda expressions.

### Syntax/Grammar
We tried to stay as true as possible to the original Python Syntax, the syntax is the same and can be used the same
as in the Python language. Some examples are below.

```python
def func1():
     v1 = func2(5)
     v2 = func2(v1)
     return func2(v2)/1

def func2(x):
     return x / 2

func1()
```

Here we see how functions are declared, they are declared the same as in the Python languages.

```python
var = 2700
print(var + 30)
print (var)
```

Here we can see how we could use the print function in python. The same as would we write it normally
while programming Python.

We could reuse a lot from the Monticore MCBasics, however some things like the Boolean and String literals needed to be
redefined since the original MCBasics definition was not correct for Python. Furthermore, the Monticore Syntax
was not made for Python which lead us to redefining a lot of things like for example indentation and multiline
comments.

### Functionality (Cocos)
In order for everything to work correctly we needed to implement some requirements in the form of Cocos.
#### 1. CallExpressionAfterFunctionDeclarationCoco
This Coco checks if the function is called after being declared. To see that we see at what position the
call is written and at what position the declaration is written and compare it to one another. Note that 
this is not checked in the cases the functions used are being imported, because in that case the position
is not relevant anymore.

#### 2. JavaBooleanExpressionCoco
This Coco blocks every type of Java booleans. For example symbols like for example && and || that mean the logical
operators 'and' and 'or' in java, can not be used in python and work. This coco blocks it and returns
the Error: InvalidBooleanExpression, the Symbol and where it was used. 

#### 3. PythonDuplicateFunctionCoco
This Coco sees if there are functions used two times in a Python file. If there are duplicate function it returns
an Error. We just check the Names of the different functions and see if there are two equal Names to return
this Error. As explained in the Evaluation Section, overloading of functions is not possible in our project.

#### 4. PythonExpressionCoco
For all Expressions that stand alone in a line, this Coco checks if the Expression is a Call or Assignment 
Expression. If it is not any of these two it returns a warning if the Statement has no effect, however it 
returns an Error if it is an Assignment Expression and no Variable declaration. This coco prevents no effect 
statements that stand there with no purpose.

#### 5. PythonFunctionArgumentSizeCoco
If we call the function we need to give the same argument size as it is defined in the function declaration.
This Coco checks whether there are at least as many arguments as the minimum number of arguments and at most 
as many as the maximum number of arguments, including the optional parameters.

#### 6. PythonFunctionOrClassDeclarationInStatementBlockCoco
It is forbidden to declare functions or classes inside an if, a for or a while. That is what this coco checks, 
it returns an Error if this rule is broken.

#### 7. PythonFunctionDuplicateParameterNameCoco
This Coco checks if in a function there are duplicate parameters as arguments. To check this we just test if there are
two parameters with the same name, in that case a Duplicate parameter name error is returned, with the position where
it happened.
#### 8. PythonLambdaDuplicateParameterNameCoco
This Coco checks if in a lambda there are duplicate parameters as arguments. To check this we just test if there are
two parameters with the same name, in that case a Duplicate parameter name error is returned, with the position where
it happened.
#### 9. PythonVariableOrFunctionOrClassExistsCoco
This Coco controls if the variable or function or class that is being used exists. To check this it simply checks the 
symbol table to see if the symbol that is being used already exits.

### Symbol Table
The Symbol Table is a table that saves all used symbols, like for example all variables, classes and functions. Whenever
there is a symbol that is being used another time, we set the reference of the duplicated symbol to the already created
symbol and continue using it like this. Furthermore, we also use this table to check whether a name of a symbol 
corresponds to a keyword in Python, to block the usage of these. We also have build in function symbols in the table,
that helps us check whether something is getting used incorrectly. Finally, for each import we search all functions
and classes in the imported classes and create corresponding symbols from those classes/functions in our table, to help
us again understand what is being used and if it is being used correctly.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

