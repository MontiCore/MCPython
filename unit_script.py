# Formats:
# speed_par: km/h
# length_par: mm
def calculate_time(velocity_par_kmh, distance_par_mm):
    distance_par_kmh = distance_par_mm / 1000000
    return distance_par_kmh / velocity_par_kmh


velocity_dmh = 3
velocity_kmh = velocity_dmh / 10000

print(velocity_kmh)

print(calculate_time(1, 5))
