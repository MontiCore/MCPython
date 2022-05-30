# This script contains examples for correct unit conversions in pythons. It
# should be seen as a template for creating an example script for a
# python-like unit language.

def calculate_time(velocity_par_kmh, distance_par_mm):
    distance_par_kmh = distance_par_mm / 1000000
    return distance_par_kmh / velocity_par_kmh


velocity_dmh = 3
velocity_kmh = velocity_dmh / 10000

print(velocity_kmh)

distance_µm = 200000
distance_dm = distance_µm / 100000

print(distance_dm)

time_min = 3
time_sec = time_min * 60

print(time_sec)

print(calculate_time(velocity_kmh, distance_dm))
