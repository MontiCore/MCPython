### Table of Content
1. Introduction (alessandra)
2. Motivation (alessandra)
    1. Problems with SIUnit calculation in software
3. Approach
    1. Description of the solution (alessandra)
    2. Languages
        1. Python (alessandra)
            1. Syntax/Grammar
            2. Functionality (cocos)
            3. Symbol Table 
        2. SIPython (lennart)
            1. Description
            2. Syntax
            3. Grammar
            4. SIUnits
            5. Functionality (cocos)
    3. Generator (lennart)
        1. description of the prettyprinter
        2. application: showing example use case
6. Discussion
    1. Evaluation of the Python/SIPython language (alessandra)
        1. MontiCore grammar creates the following problems.
        2. Other Problems
    2. Comparison (lennart)
        1. with using plain Python + SiUnits library (pint)
    3. Evaluation of using python for unit calculation (lennart)
        1. It's not as efficient as other programming languages
        2. no type checking at compile time
    4. Not implemented python features (alessandra)
7. Conclusion (alessandra)
    1. summary of previous chapters

# Introduction
The developed project is a python language software that offers support for calculation with si units, 
automated unit compatibility checking, and unit conversion. It supports a verity of SI units, a few examples
are electric units and every type of time units.  
## Motivation
Nowadays most businesses use software in order to improve the revenue and facilitate life. We see software
in most of our day-to-day life, and while software is used in most things nowadays, it is not yet adapted
for all areas. In the scientific environment, even thought not always as obvious is an area that uses 
software as one of the most important tools for facilitating research and production.
However, most common languages, like java or python do not support scientific programming. This results in many problems, 
one of the most common issues arise while using metrics, for example si units. The software does not support it
which means that humans do the conversion, which can lead easily to human errors. Furthermore, whenever scientists
want to check and maybe even continue the work of colleagues, it is not necessarily known what type of si units
the previous scientists used, and it leads to a lot of confusion and mistakes. Mistakes that are not always easy to 
solve or to come back from, the equipment that is in risk is mostly very costly and a little conversion error can go
a long way.

The Mars Climate orbiter was a 638-kilogram robotic space probe launched by NASA to study Martian climate, atmosphere, 
and surface changes. However, little time after launch the space probe got lost. An investigation was opened to study 
the reasons of this very expensive failure. This investigation finally attributed the failure to a mismatch between two
metric systems. The piece of ground software supplied by 'Lockheed Martin' produced results in a United States customary
unit, however contrary to that, its Software Interface Specification, supplied by NASA was expecting results to be in 
SI Units, in accordance to their Software. The results were produced in pound-force seconds while it was expected to be 
in newton-seconds.[1]

An issue that cost millions of damage could have been very easy to solve. Having a software that supports conversion of
metrics would just automatically produce the results in the wished system, avoiding any type of further problems. 
Building such a system will be our goal in this project. The result should be a software that supports programming in 
python while using si units.
[1] https://en.wikipedia.org/wiki/Mars_Climate_Orbiter

## Tools
To achieve the goal we used a variety of tools.
### Monticore
Monticore is a language workbench for the efficient development of domain-specific languages. Monticore enabled
us to define our language, Python, and use it together with the MontiCore-framework to build domain specific
tools, like for example integrating our SI Units in the language. We could take advantage of the predefined language 
components that Monticore comes with. Monticore creates the basic grammar and provides all components (statements, literls, etc.).
Monticore then generates a parser that can check if the si python scripts are correct.
[2] https://monticore.github.io/monticore/
### SI Units Project 
The SI Units Project sets the grammar so that it can be read by Monticore, it mainly describes which units are valid and specifies 
what can be used as metric units.
It builds on top of Monticore and allows the parsing of si units, si expressions and pretty printers. 
### Pint 
Pint is a Python package to define, operate and manipulate physical quantities. It allowed us to implement arithmetic
operations between them and conversions from and to different units. It supports a lot of numpy mathematical operations
without monkey patching or wrapping numpy.Important to note it runs in Python 3.8+ with no other dependencies[3] Pint's 
main job will be checking if the units match, if they can be converted and if the types match. Thanks to pint we can 
avoid conversion errors like those that happened with the Mars Climate Orbiter.
[3] https://pint.readthedocs.io/en/stable/
# Approach
## Description of the solution
We used 3 main tools. Monticore, the SI Units project and the Pint library. We also worked on two grammars SIPython adds
the SIUnits from the SIUnits library, it also adds special si expressions like for example conversions. Not only did we define
the grammar we also defined Pretty printers to be compatible with the pint library. We also implemented Cocos to check statements
that we defined and need checking.
## Languages

### Python
The Python grammar is a limited Python-like grammar. 
Python grammar allows us to parse python scripts. This grammar supports Statements, Literals and Expressions.
The statements we define and consequently are supported by the grammar,
are the import statements, the if-else statements, the assert statements, the for while statements, the break statements, 
the try-except-finally statements, the with open statements, variable declarations and assignments including arrays
and tuples (only with brackets, more information found in the Problems section), the return statement. Functions and 
classes are also supported. We also needed to define String Literals and Boolean Literals since they were inconsistent 
to the implementation defined on MCBasics. Lastly we also define Expressions, it supports the ternary-operator
expression, mathematical and logical expressions and finally also lambda expressions.
#### Syntax/Grammar
We tried to stay as true as possible to the original Python Syntax, the syntax is the same and can be used the same
as in the Python language. Some examples are below.

```python
def func1():
     v1 = cm(5 dm)
     v2 = mm(v1)
     return func2(v2)/1 m

def func2(x):
     return dm/h(x / 2 ms)

func1()
```

Here we see how functions are declared, they are declared the same as in the Python languages, however
here we can use SIUnits, cast them and convert them or do operation with them.

```python
var = h(7200 s)
print(20 km + 30m)
print (var)
```

Here we can see how we could use the print function in python. The same as would we write it normally
while programming Python, however here we can use SIUnits, conversions or operations.

We could reuse a lot from the Monticore MCBasics, however some things like the Boolean and String literals needed to be
redefined since the original MCBasics definition was not correct for Python. Furthermore, the Monticore Syntax
was not made for Python which lead us to redefining a lot of things like for example indentation and multiline
comments.

#### Functionality (Cocos)
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

#### Symbol Table
The Symbol Table is a table that saves all used symbols, like for example all variables, classes and functions. Whenever
there is a symbol that is being used another time, we set the reference of the duplicated symbol to the already created
symbol and continue using it like this. Furthermore, we also use this table to check whether a name of a symbol 
corresponds to a keyword in Python, to block the usage of these. We also have build in function symbols in the table,
that helps us check whether something is getting used incorrectly. Finally, for each import we search all functions
and classes in the imported classes and create corresponding symbols from those classes/functions in our table, to help
us again understand what is being used and if it is being used correctly.
    
# Discussion

## Evaluation of the Python/SIPython language
We tried to define a Python language that works exactly like Python except we can use different types of SIUnits,
we were successful to a certain extent, however there still remain some problems.
## MontiCore grammar creates the following problems:
#### 1. Integer Division
When we need to divide 2 numbers in python we separate the two numbers or the two variables by two forward slashes. Two
forward slashes are also used to initialize a comment which is a problem for the component grammar MCBasics. MCBasics 
recognizes the two forward slashes as a start of a single line comment. The integer division is just possible with spaces
around the two forward slashes. Example: 3 // 2

#### 2. Indentation
Monticore grammar skips all ident tokens, we can see this on the MCBasics component, as shown by the figure
below spaces, tabs, paragraphs and carriage returns are skipped since they are not needed. 

```python
token WS =
    (' ' | '\t' | '\r' | '\n' ) : -> skip;
```

However, we need to know when indentation is used and how it is used, 
in order to check if it is used correctly. We also need
to save the indentation in order to use it in our printer and print it correctly.
To solve this problem we added some things to our grammar in order to not skip the indentation, we define
what type of indentation that should not be skipped and implemented functions for functionality.
We implemented an indentation counter in our grammar that checks if the indentation is done
correctly. 

#### 3. Monticore's incompatibility for the Python language 
Monticore is based on the java language, which gave us some incompatibility problems.
The Boolean literals in Monticore are defined, for example, in order to adapt to the java standard of 
boolean values, the difference between booleans on java and booleans on python is the first letter: 'True'
is used in Python, however in Java we use 'true' the same goes for the false statement. The same goes for
the String literals, even though in java we would just use one type of quotation marks: "", in Python 
we can use another type of quotation marks: ''. 

To fix this we defined string literals and boolean literals in our grammar, these literals are
adapted to Python by implementing the differences already mentioned above. 

However, there still remains a problem, since we can not overwrite the original 
grammar the words 'true' and 'false' even though not meaning anything in Python, remain keywords and
can not be used as variable names, which should be working since they do not mean anything in Python. The
only solution is to not use this types of variables.

## Other Problems:

#### 1. Tuples without parentheses
In Python, we can use tuples with different number of arguments and with or without parentheses. An example 
to visualize this would be using this type of tuple: var = (1,4,5), this works in Python and can be used without
problems like this, however, we can also use it like this: var = 1,4,5. When printing this tuples like this:
print(var), the same result should appear: the tuple with parentheses, like this: (1,4,5). However, this
was impossible for us to implement due to a 'forbidden left recursion' error that is thrown. We suspect the 
reason for this issue being that context free grammars can not be recursive on the left of statements.

#### 2. End of File 
Our statements always end with an End of Line token. An End of Line token consists of one or more paragraphs or
carriage returns, or both. We defined this in our grammar for all statements. However, when the file ends
we do not have an end of line token. In this case we would need an End of file token in order for a problem
not to arise at the end of files. Nonetheless, the end of file token did not work for us even though
we tried a set of 
variations of how to define it. 

# Not implemented python features
There are some Python features that are not implemented at all in our project.

#### 1. Function Overload
Function overloading is a feature that exists in a lot of programming languages. It is using different
functions with different implementations, however with the same name. Depending on the context that the
function is called, it executes the different implementations. An example would be: doSomething() and
doSomething(Obj o), this two functions have the same name however are considered as two different functions,
normally with different types of implementations, one not needing an object and the other one needing it.
Another example could be: doSomething(String s) and doSomething(Int i), here we change the types of the
arguments in the function, and considering these types the functions do different things.

We could not implement this in our project. To succeed in implementing this feature, the symbol table
must be adapted to prevent duplicate
function symbols with the same name. However, symbol merging is not recommended since in this case,
scopes would be merged together and so would also all variables. Further checks would be impossible to
make and this prevented us to implement this feature. The only solution around this is to not
use this feature.

#### 2. Del/global/nonlocal/yield keywords
These keywords are not as used as other keywords that we did implement, priority was given to the keywords that are
most used in the Python language since we did not have enough time resources to implement everything.

#### 3. Multiline comments
For the multiline comments we have a problem of compatibility. The comments in Python begin and end with quotation marks,
this is valid for two different types of quotation marks: the single quotation mark (') and the double quotation
mark ("). However defining the comments with the double quotation mark
in our grammar was impossible, it is confused for a String Literal and outputs an 
error, since we use 3 times the quotation marks between comments, which seems like they are uncompleted Strings. We
tried to implement a Coco and tried to define the grammar differently, however nothing worked. We could not overwrite 
this issue so that it would not be read as String Literals and decided to not implement this feature. The solution
for this should to basically use the one line comments repeatedly to create a kind of type of multiline comment or 
we just use the single quotation mark to create multiline comments.

## Comparison

## Evaluation of Unit Calculation with Python

# Conclusion