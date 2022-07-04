from pint import UnitRegistry

ureg = UnitRegistry()

v = 24.0 * ureg('m/h')
print(v)
