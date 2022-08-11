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

class NewSIUnitLiteral
Literal <|-- NewSIUnitLiteral
note right: e.g. 3 dm/h
```

The new si unit literal itself should consist of a numeric literal indicating the value of the literal, and a nonterminal representing the si unit, as shown in the following diagram.

```plantuml
class NumericLiteral <<(N,transparent)>>   
note bottom: e.g. 3

class NewSIUnitNonTerminal
note bottom: e.g. dm/h

class NewSIUnitLiteral
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

Now we are able to specify literals containing si units. However, the ability of specifying si unit literals is not sufficient for a si unit supporting programming language. As with normal literals like strings or integers, programmers require to perform conversions between types. Conversions between types can be implicit or explicit. In implicit conversions, the types are automatically converted depending on their use in the code. The following code shows an example of an automatic conversion in python.

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

---------------------------------------------------------------------------------------------------------------------

To support si unit literals, we identified the following requirements for the SIPython language:
1. **Use of si units in literals**\
   As previously described the language has to support the utilization of si units in expressions.
2. **Conversion between si unit literals**\
   In addition to the use of si unit literals in expression, it must be possible to specify conversions between si unit literals.\
   Such a conversion expression can be used to e.g.  convert a value with the si unit dm/h to m/s.
3. **Compatibility checking of si unit literals**\
   Furthermore, with the use of multiple si unit literals in the expressions, their compatibility has to be checked.\
   The following code snippets shows an example expression containing the two si units dm/h and °C, which are not compatible and would lead to a useless result when executing the expression.
   ```python
    v = 3 dm/h + 5 °C
    ```
   Thus, the SIPython language has to support a compatibility checking for expressions to detect incompatible si units.

To support the use of si unit literals, the SIPython language extends the language SIUnitLiterals of the [SIUnits project](https://git.rwth-aachen.de/monticore/languages/siunits). The SIUnits project provides a type system for si units. The SUnit language offers an extension of MontiCores [MCCommonLiterals](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/literals/MCCommonLiterals.mc4) grammar for si units.

---------------------------------------------------------------------------------------------------------------------

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