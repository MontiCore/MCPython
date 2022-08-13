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
        2. no type checking at compile time
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
components that Monticore comes with. Monticore comes with an extension.[2]
[2] https://monticore.github.io/monticore/
### Pint  
Pint is a Python package to define, operate and manipulate physical quantities. It allowed us to implement arithmetic
operations between them and conversions from and to different units. It supports a lot of numpy mathematical operations
without monkey patching or wrapping numpy.Important to note it runs in Python 3.8+ with no other dependencies[3]
[3] https://pint.readthedocs.io/en/stable/
# Approach

## Languages

### Python

### SIPython

## Generator

# Discussion

## Evaluation of the Approach

## Comparison

## Evaluation of Unit Calculation with Python

# Conclusion