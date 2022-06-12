# SIPython

This project encompasses a grammar for a Python-like language with si unit enriched types.

## Syntax

To specify the si unit of a value, the type is written after the value.

**Examples**:

<code>var = 3 dm/h</code>

<code>some_method_call(3 km/m)</code>

To cast a value to another si unit, the language provides suited cast methods.

**Example**:

`var1 = 3 dm/h`

`var2 = m/h(var1)`