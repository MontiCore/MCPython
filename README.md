# SIPython

The SIPython language serves a Python like language with si-units. A generator takes models from the language and computes python scripts.

## Requirements
The following python packages are required to run the generator:
- `keywords`
- `inspect`
- `builtins`
- `types`

All packages can be installed via `pip install <package>`.

## [Python.mc4][PythonGrammar]

### Description

The Python grammar is a limited Python-like grammar. This grammar supports variable declarations and assignments including arrays and tuples (only with brackets).
Functions and classes are also supported.

### Indentation
The grammar recognizes 4 spaces or a tabulator symbol as an indent. Three or fewer spaces at a beginning of a line will be ignored. 

### Special cases in contrast to Python
- A model must end with an empty line to be parsed correctly.
- The integer division must be used with spaces, like `3 // 2` not `3//2`
- Function overloading is not possible.

### Functionality

**[PythonFunctionArgumentSizeCoco.java][PythonFunctionArgumentSizeCoco]**

**[PythonFunctionDeclarationInStatementBlockCoco.java][PythonFunctionDeclarationInStatementBlockCoco]**

**[PythonFunctionDuplicateParameterNameCoco.java][PythonFunctionDuplicateParameterNameCoco]**

**[PythonVariableOrFunctionOrClassExistsCoco.java][PythonVariableOrFunctionOrClassExistsCoco]**


### Symbol Table

Each python script spans a symbol scope. In this scope there can be the Symbols `VariableSymbol`, `FunctionSymbol` or `ClassSymbol`.
For each variable declaration a `VariableSymbol` is being created. 

## [SIPython.mc4][SIPythonGrammar]

## Syntax

To specify the si unit of a value, the type is written after the value.

**Examples**:

`var = 3 dm/h`

`some_method_call(3 km/m)`

To cast a value to another si unit, the language provides suited cast methods.

**Example**:

`var1 = 3 dm/h`

`var2 = m/h(var1)`

## Functionality

TODO cocos

## Generator

The generator uses the PrettyPrinter to print python scripts. To keep the si units the generator uses the `print` library of python.
The library provides si unit types via objects in python. `pint` also converts values, so that they are compatible. 
If multiple values are not compatible, the `pint` library will throw an exception.
For SI unit types there are a few conversions to be done:

- Variable declaration and assignment:
  - `var = 3 m/s` → `var = 3 * ureg('m/s')`
  - `var1 = 1 km/h + var` → `var1 = 1 * ureg('km/h) + var`


## Generator-Tool

`java -jar sipython-0.0.1-SNAPSHOT-tool.jar <src> <dest> [options]`

**Arguments**

**Options**
- `--execute <script>`: Executes the given script after generating the python scripts. 



[PythonGrammar]: src/main/grammars/de/monticore/Python.mc4
[SIPythonGrammar]: src/main/grammars/de/monticore/SIPython.mc4

[PythonFunctionArgumentSizeCoco]: src/main/java/de/monticore/python/_cocos/PythonFunctionArgumentSizeCoco.java
[PythonFunctionDeclarationInStatementBlockCoco]: src/main/java/de/monticore/python/_cocos/PythonFunctionDeclarationInStatementBlockCoco.java
[PythonFunctionDuplicateParameterNameCoco]: src/main/java/de/monticore/python/_cocos/PythonFunctionDuplicateParameterNameCoco.java
[PythonVariableOrFunctionOrClassExistsCoco]: src/main/java/de/monticore/python/_cocos/PythonVariableOrFunctionOrClassExistsCoco.java