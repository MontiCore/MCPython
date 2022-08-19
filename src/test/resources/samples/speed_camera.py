from pint import UnitRegistry
ureg = UnitRegistry()
# This sample script is an implementation of a speed camera.
# The script reads the measured values from a file and calculates the velocity.
# For a more precise measurement, the file contains two time values for two measuring sections.
# If both speeds are above the limit, the script prints "flash",
# else if one speed is above the limit, the script prints "unhappy",
# else the script prints "happy".
def calcSpeed(distance, time):
    return distance / time
limit = 50 * ureg('km/h')
distance1 = 5 * ureg('m')
distance2 = 2 * ureg('m')
measurements = open("measurements.txt")
time1 = -1
time2 = -1
# Read the two measured times from the file line by line
for i, line in enumerate(measurements):
    if i == 1:
        time1 = 1 * ureg('ms') * line
    elif i == 2:
        time2 = 1 * ureg('ms') * line
if time1 == -1 or time2 == -1:
    raise RuntimeError("Invalid measurements!")
speed1 = calcSpeed(distance1, time1)
speed2 = calcSpeed(distance2, time2)
if speed1 > limit and speed2 > limit:
    print("flash")
elif speed1 > limit or speed2 > limit:
    print("unhappy")
else:
    print("happy")
