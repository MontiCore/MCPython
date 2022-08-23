
## Motivation and Basic Example
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

To show how the generated Python script of a SIPython script looks like, the following snippet displays an example SIPython script.

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

# casting/parsing
v = km/h(3 dm/h)
v1 = km/h(y)

v2 = 4 m/ns^2

print(velocity)

distance = 200000 µm

print(distance)

time = 3 min

print(time)

print(calculate_velocity(distance, time))

class calculator:

    def __init__(self):
        self.factor = 1

    def multiply(self, x):
        return self.factor * x

c = calculator()

print(c.multiply(1))

```
Using the described Generator class, the following Python script is generated.

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
calculate_velocity(distance = 1, time = 5)
velocity = 3 * ureg('dm/h')
y = velocity + 1 * ureg('m/s')
v = 3 * ureg('km/h')
v1 = y * ureg('km/h')
v2 = 4 * ureg('m/ns^2')
print(velocity)
distance = 200000 * ureg('µm')
print(distance)
time = 3 * ureg('min')
print(time)
print(calculate_velocity(distance, time))
class calculator:
    def __init__(self):
        self.factor = 1
    def multiply(self, x):
        return self.factor * x
c = calculator()
print(c.multiply(1))

```

[1] https://en.wikipedia.org/wiki/Mars_Climate_Orbiter


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)



