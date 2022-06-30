# Conversion of variables with unit types to python variables

Sipy Script:
var1 = 3 dm/h;
var2 = 30 cm/h
var1 = var1 + var 2
var1 = cm/h(var1)

Python Script:
var1 = (3,'dm/h')
var2 = (30, 'cm/h')
var1 = (var1[0] + var2[0] * 0.1, var1[1])
var1 = (var1[0] * 10, 'cm/h')