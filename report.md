### Table of Content
1. Introduction (alessandra)
2. Motivation (alessandra)
    1. Problems with SIUnit calculation in software
3. Approach
    1. Description of the solution (alessandra)
    2. Languages
        1. Python (alessandra)
            1. Description
            2. Syntax
            3. Grammar
            4. Indentation
            5. Functionality (cocos)
            6. Symbol Table
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
        1. problems
    2. Comparison (lennart)
        1. with using plain Python + SiUnits library (pint)
    3. Evaluation of using python for unit calculation (lennart)
        1. It's not as efficient as other programming languages
        2. no typechecking at compile time
7. Conclusion (alessandra)
    1. summary of previous chapters

# Introduction

---

# Motivation

---

# Approach

---

## Languages

### Python

---

### SIPython
With the previously presented language, it is possible to parse python scripts. However, only literals like strings,
characters, numbers, and booleans are supported, as shown in the following code snippet.
```python
c = 'a'
n = 3
b = true
```
The support for si unit literals is still missing. The following code snippets shows an example of a variable assigment expression including a si unit literal, as expected in a python like scripting language.
```python
v = 3 dm/h
```
Therefore, the language SIPython was created, which extends the Python language. As described in the previous section it adds support for si unit literals. For such a language to be used in practice it had to fulfill certain requirements, that we had to identify and solve, illustrated in the following.

#### SI Unit Literals

The first requirement, as previously outlined, is the support for si unit literals. The basic approach would be to introduce a new literal type extending MontiCores Literal nonterminal of the [MCLiteralBasis](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/literals/MCLiteralsBasis.mc4) grammar. This would allow to directly include the new si unit literal in all kinds of expressions extending the Expression nonterminal of the [ExpressionBasis](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/ExpressionsBasis.mc4) grammar. The described approach with its grammar dependency is shown in the following diagram. Here, the new introduced nonterminal named _NewSIUnitLiteral_ allows to model literals containing si units like `3 dm/h`. Through, the extension of the _Literal_ nonterminal, which in part of the _Expression_ nonterminal it would be possible to express e.g. multiplication expression containing si units like `3 dm/h * 5`.

```plantuml
package "MontiCore" {
package "MCLiteralBasis " {
    class Literal <<(N,transparent)>>
}

package "ExpressionsBasis" {
    class Expression <<(N,transparent)>>
    note right: e.g. 3 dm/h * 5
    
    Expression o-- Literal
}
}

class NewSIUnitLiteral  <<(N,transparent)>>
Literal <|-- NewSIUnitLiteral
note right: e.g. 3 dm/h
```

The new si unit literal itself should consist of a numeric literal indicating the value of the literal, and a nonterminal representing the si unit, as shown in the following diagram.

```plantuml
class NumericLiteral <<(N,transparent)>>   
note bottom: e.g. 3

class NewSIUnitNonTerminal <<(N,transparent)>>
note bottom: e.g. dm/h

class NewSIUnitLiteral <<(N,transparent)>>
note right: e.g. 3 dm/h

NewSIUnitLiteral o-- NumericLiteral
NewSIUnitLiteral o-- NewSIUnitNonTerminal
```


However, the MontiCore team already provides such a nonterminal, as part of their [SIUnits project](https://git.rwth-aachen.de/monticore/languages/siunits). The SIUnit project introduces a full type system for si units to be used in other MontiCore languages. Their approach for modeling si unit literals as a MontiCore grammar corresponds to the previously described approach. The following diagram shows an overview of the defined grammars and their dependencies in the MontiCore domain.

```plantuml
package "MontiCore" {
package "MCLiteralBasis Grammar" {
    class Literal <<(N,transparent)>>
}

package "MCCommonLiterals Grammar" {
    class NumericLiteral <<(N,transparent)>>   
    note bottom: e.g. 3
    
    Literal <|-- NumericLiteral
}
}

package "SIUnit-Project" {
package "SIUnits Grammar" {
    class SIUnit <<(N,transparent)>>   
    note bottom: e.g. dm/h
}

package "SIUnitLiterals Grammar" {
    class SIUnitLiteral <<(N,transparent)>>
    note bottom: e.g. 3 dm/h
    
    Literal <|-- SIUnitLiteral
    SIUnitLiteral o-- NumericLiteral
    SIUnitLiteral o-- SIUnit
}
}

```

In the SUnit project, the literal for si units is modeled by the _SIUnitLiteral_ nonterminal. As previously described, it extends the _Literal_ nonterminal of the _MCLiteralBasis_, and is composed of the _NumericLiter_ of the [MCCommonLiterals](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/literals/MCCommonLiterals.mc4) grammar. To model the si unit of the literal, the _SIUnitLiteral_ is additionally composed of the _SIUnit_ nonterminal. _SIUnit_ covers all kinds of si units, including primitive ones like ``dm``, and the division of primitive ones like `dm/h`, as well as the division of a numeric literal and a primitive si unit like `1/h`.

To make use of this approach, we let the SIPython grammar extend the SIUnitLiterals grammar. This leads to the language composition, as shown in the following diagram.

```plantuml
package "SIUnitLiterals Grammar" {
    class SIUnitLiteral <<(N,transparent)>>
}

package "SIPython Grammar" {

}

package "Python Grammar" {
}

"Python Grammar" <|-- "SIPython Grammar" 
"SIUnitLiterals Grammar" <|-- "SIPython Grammar" 
```

#### SI Unit Conversions

Now we are able to specify literals containing si units. However, the ability of just specifying such literals is not sufficient for a si unit supporting programming language. As with normal literals like strings or integers, programmers require to perform conversions between types. Conversions between types can be implicit or explicit [^1]. In implicit conversions, the types are automatically converted depending on their use in the code. The following code shows an example of an automatic conversion in python.

[^1]: https://www.programiz.com/python-programming/type-conversion-and-casting

```python
num_int = 123
num_flo = 1.23

num_new = num_int + num_flo
print(num_new)
>>> 124.23
```

Hereby, a calculation between variables of different types, integer and float, is performed. The lower type integer is automatically converted to the higher type float. The result is therefore of type float as well.

In explicit conversion of types, the conversion is explicitly specified in the code. As shown in the following code snippet, explicit conversions in python are performed using special functions.

```python
num_str = int("456")
print(type(num_str))
>>>"<class 'int'>"
```

In this example, a string value is converted to an integer value and assigned to a variable. The variable is now of type integer.

As our language introduces new literals, we have to support implicit and explicit conversions for si units as well.

Although, the SIUnit project introduces si unit literals, it only provides functionalities for implicit conversions of si units for statically typed languages [^2]. This means, it is only usable in static typed languages like Java, where the type of variables are specified in the declaration and never change during runtime.

[^2]: https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/dev/src/main/grammars/de/monticore/SIUnits.md

However, this is not applicable for a python like language, as it is dynamically typed. Hereby, the python interpreter does not necessarily know the type of variables. The following code snippet shows an example situation where implicit type conversions can not be performed during compile time, due to the lack of type information.

```python
def some_method(var1, var2):
    return var2 + var2

some_method(4,5.3)
```

In this snippet, a method declaration with two input parameters is displayed. As the type of the variables are not provided, no type conversions can be performed during the calculation of both paramteres in the input body. Thus, implicit type checking implemented by the SIUnit project is not applicable for the SIPython language.

##### Explicit Type Conversions

Explicit conversions of si units is not supported at all by the SIUnit project. Thus, we had to come up with an own approach for type conversions. This approach is displayed in the following diagram.

```plantuml
package "ExpressionBasis Grammar" {
    class Expression <<(N,transparent)>>
}

package "Python Grammar" {
    class VariableInit <<(N,transparent)>>
}

package "SIUnitTypes4Math" {
    class SIUnitType <<(N,transparent)>>
}

package "SIPython Grammar" {
    class SIUnitConversion <<(N,transparent)>>
    note right: e.g. km/h(3 dm/h)
    VariableInit <|-- SIUnitConversion
    Expression <|-- SIUnitConversion
    SIUnitConversion o-- Expression
    SIUnitConversion o-- SIUnitType
}
```

Here, explicit conversions are modeled by the _SIUnitConversion_ nonterminal. Because, conversions can be used as a normal expression, as well as a variable initialisation, _SIUnitConversion_ implements _VariableInit_ of the _Python_ grammar, as well as _Expression_ nonterminal of the _ExpressionBasis_ grammar. We defined the _SIUnitConversion_ nonterminal itself in the SIPython grammar as follows.

```
SIUnitType "(" Expression ")"
```

An explicit conversion can be made by specifying the target unit of the conversion followed by an expression containing si units. This includes conversions like ``km/h(3 dm/h)`` as well as ``km/h(3 dm/h + 5 m/s)``.

##### Implicit Type Conversions

For implicit conversions we had to come up with an approach at runtime. The only way to archive this is to include specific code in the python script, which is generated from the SIPython script, that is responsible for the conversion of si units. Such code could look like the following pseudocode snippet.

```python
def operation_with_implicit_conversion(var1, var2, operator) {
    higher_type = get_higher_type(var1,var2)
    return operator(convert(var1,higher_type), covert(var2,higher_type))
}

sum = operation_with_implicit_conversion(34,12.34,add)
print(sum)
>>> 46.34
```

Hereby, to perform operations on variables, a special function has to be called, that takes the variables and the operator as input and performs the implicit conversion as previously described. For this approach, it is required, that the si unit of a variable is preserved. For Python this could be archived, by specifying variables as pairs of value and type in the generated Python code. E.g. the SIPython code ``v = 3 dm/h`` would be converted to ``v = (3,"dm/h")``.

However, the described approach for implicit conversions of si units is already implemented by various python libraries, e.g. [siunits](https://pypi.org/project/siunits/), [forallpeople](https://github.com/connorferster/forallpeople), or [astropy](https://uomresearchit.github.io/programming_with_python/06-units_and_quantities/index.html). These libraries introduce new si unit object types and associated conversion and type checking functionalities. For our project we chose the [pint](https://pint.readthedocs.io/en/0.10.1/) library, as it provides an easy-to-use interface, especially important in the code generation process, described in the [Generator section](#generator). The pint, library is imported in the generated python code, and variables are initialized as pint unit objects. When performing calculations on those objects, the pint library performs the implicit conversion automatically.

With the described approach of introducing a explicit conversion nonterminal _SIUnitConversion_, and performing implicit conversions using si unit python libraries in the generated code, we managed to provide type conversion suited for a python like language supporting si units.

#### Compatibility Checking
To this point we described, how we managed to provide si unit literals for our language and to perform type conversions on si units. The third requirement for our SIPython language, is to provide compatibility checking of multiple si unit literals used in the expression. Code like ``3 dm/h + 5 °C`` would lead to unusable results, as the specified types of the addition expression are not compatible. Although, the SIUnit project already provides a compatibility checks for si units, the same problem as for type conversions remains. Only compatibility checks for statically typed languages are provided. As our approach has to be dynamically typed, we can not make use of the provided functionality. However, we can make use of the pint library, as it provides a full type checking system for si units at runtime.

The resulting approach covered all identified requirements for a python like programming language that supporting si unit.

---

## Generator

In the previous section, we illustrated our approach for a Python-like programming language that provides support for si units, containing of the two grammars _Python_ for Python scripts and _SIPython_, an extension of *Python* adding support for si units. In this section, we describe the developed tooling supporting the utilization of the language in parsing and generating python scripts. Our approach follows the recommended model processor architecture, as recommended by the MontiCore team. Its implementation was inspired by the [si unit supporting Java-like programming language](https://git.rwth-aachen.de/monticore/languages/siunits/-/tree/dev/src/test/java/de/monticore/lang/testsijava), developed as part of the SIUnit project.

The main entry class is the [Generator](\src\main\java\de\monticore\sipython\generator\Generator.java). It allows to generate a python scripts from a SIPython scripts. Example use cases are provided in the [GeneratorTest](\src\test\java\de\monticore\sipython\GeneratorTest.java).

The tasks of the Generator class are displayed in the following diagram. First, it parses the input model script, creating an abstract syntax tree, using the generated SIPythonParser class. Next, the symbol table for the input model, as well as imported files and built-in python functions is created. Afterwards, the ast tree is checked against the context conditions, and finally, the output file is generated using the implemented pretty printer.

```mermaid
graph TD;
subgraph Generator Tasks
id1(Parsing)
id2(Symbol Table Creation)
id3(CoCo Checking)
id4(Output Generation)
id1-->id2
id2-->id3
id3-->id4
end
```

##### Generation of Python Script
As described, the last task of the Generator class is to generate the output Python script. Hereby, the visitor architecture, provided by MontiCore, is used to traverse the parsed AST tree of the input model, and pretty-printing the AST nodes of the input model as valid python expressions. Thus, we had implemented for each generated AST node type of the grammar elements, a corresponding printer function.

For the elements of the Python grammar, the implementation of such printer functions was trivial as the output had to remain the same as the input. In case of printing _SIUnitLiterals_ and _SIUnitConversions_ of the _SIPython_ grammar, the previously described application of the pint library had to be considered.

Thereby, to ensure that the type checking and conversion for si units was performed when running the generated Python script, the si unit objects had to be used when printing _SIUnitLiterals_ and _SIUnitConversions_. Using the pint library si unit objects are created by multiplying the value of the unit with the unit type itself. In pint the unit type can be specified by constants or using a look-up function, that retrieves the type from a passed input string, as shown in the following code snippet.

```python
3 * ureg.meter
3 * ureg("m")
```

Both expressions create si unit objects for the literal ``3 m``.

For printing SIUnitLiterals, we decided to take the later variant of creating si unit objects, using the unit look-up function. This is because, in the created AST nodes of SIUnitLiterals the, the si unit type is provided as a string. Thus, when printing the unit the si unit type just has to be inserted in the look-up function. The following code snippet shows a SIUnitLiteral and its printed python expression using the pint library, as described.

```python
#SIPython code
3 dm/h
```
```python
#generated Python code
3 * ureg("dm/h")
```

In the same way, we implemented the printing of _SIUnitConversions_. For a given conversion expression the input of the conversion is printed, multiplied with the type of the conversion as an ureg expression, as shown in the following example.

```python
#SIPython code
km/h(y)
```
```python
#generated Python code
y * ureg('km/h')
```

To be able to use the pint library each generated scripts contains a corresponding import statement and an intitalisation statement for the ureg function, as shown in the following snippet.

```python
from pint import UnitRegistry
ureg = UnitRegistry()
```

Thus, given an example SIPython script, like the following one:

```python
# This is an example script of a unit type supporting python-like language
x = true
import calendar
print(_monthlen(2022, 07))
print("Hello World")
def calculate_velocity(distance, time):
    return distance / time
calculate_velocity(x=1, time=5)
velocity = 3 dm/h
y = velocity + 1 m/s
v1 = km/h(y)
v2 = 4 m/ns^2
print(velocity)
print(calculate_velocity(distance, time))
class calculator:
    def __init__(self):
        self.factor = 1

    def multiply(self, x):
        return self.factor * x
c = calculator()
print(c.multiply(1))
```
Using the described Generator class, the following Python script is generated:

```python
from pint import UnitRegistry
ureg = UnitRegistry()
# This is an example script of a unit type supporting python-like language
x = true
import calendar
print(_monthlen(2022, 07))
print("Hello World")
def calculate_velocity(distance, time):
    return distance / time
calculate_velocity(x = 1, time = 5)
velocity = 3 * ureg('dm/h')
y = velocity + 1 * ureg('m/s')
v1 = y * ureg('km/h')
v2 = 4 * ureg('m/ns^2')
print(velocity)
print(calculate_velocity(distance, time))
class calculator:
    def __init__(self, self):
        self.factor = 1
    def multiply(self, x, self):
        return self.factor * x
c = calculator()
print(c.multiply(1))
```

##### Generator-Tool
To further ease the use of the Generator, we provide a tool that automatically parses a SIPython script, and runs the Python script generated by the Generator class. To use this, the main method of the [Main](\src\main\java\de\monticore\sipython\Main.java) class has to be executed. Hereby, the input SIPython script has to be specified with the following string as input to the main method ``--execute <script>``. With this it is possible to provide an executable jar to possible users of the SIPython language.

---

# Discussion

## Evaluation of the Approach

---

## Comparison between SIPython with plain Python
Following the evaluation of our approach, it has to be examined how usable it is in comparison to the use of plain python with si unit supporting libraries like pint. As this would be the closest alternative for software engineerings developing software containing si units.

By utilizing our SIPython language the programming of code containing si units becomes way more intuitive. Our approach of specifying the si unit after the value is also used in natural language, in contrast to the si unit pint expressions. As the following example shows our approach of specifying si unit literals is closer related to natural language than the unit expression of the pint libraries.

```python
#SIPython code
velocity = 3 dm/h

#Natural language
"The velocity is 3 dm/h"

#Python code with pint library
velocity = 3 * ureg("dm/h")
```

This is especially beneficial for developers with less software engineering experience. In relation to the intuitivity, the readability of the code increases as well, by using the SIPython language. As the following example shows, by using the pint library, the code contains an additional multiply operation and a method call for ech si unit, increasing the line length.

```python
#SIPython code
velocity = 3 dm/h + 5 cm/s - 20 m/h

#Python code with pint library
velocity = 3 * ureg("dm/h") + 5 * ureg("cm/s") - 20 * ureg("m/h")
```

On the other side, with the SIPython language, additional steps have to be performed, when executing code. When a programmer wants to run its SIPython code, it has to be parsed, analyzed and converted to python code in addition to the steps of the python interpreter that runs the generated python code. This increases the development process, when testing the developed code. However, the production code should only consist of the generated Python code rather than the SIPython code, to reduce its execution times.


Furthermore, the SIPython language lacks of support for IDEs. Currently, there is e.g. no code highlighting, error checking at development time, or debugging functionality provided. Therefore, developer rely on generating the python code after each change, to use the lacking functionalities.

The comparison of using SIPython language and using Python with the pint library, we showed that the resulting SIPython code increases the readability and creates more intuitive code. Which is especially important for unexperienced developers. However, it also shows that additional support for development tools is required, to make it usable in software development.

---

## Evaluation of Unit Calculation with Python

---

# Conclusion