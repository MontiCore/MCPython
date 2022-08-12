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

The resulting approach covered all identified requirements for a python like programming language that provides si unit support.

#### Implementation of SIPython
However, the implementation of the SIPython using the MontiCore framework had to be adapted, to ensure its functionality.

---

## Generator

---

# Discussion

## Evaluation of the Approach

---

## Comparison

---

## Evaluation of Unit Calculation with Python

---

# Conclusion