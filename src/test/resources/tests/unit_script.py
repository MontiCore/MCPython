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
