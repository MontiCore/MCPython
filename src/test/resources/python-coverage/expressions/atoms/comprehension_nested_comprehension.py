# Reference: https://docs.python.org/3.15/reference/expressions.html#complex-comprehensions
# Grammar:   listcomp
# PEP:       202
# Since:     Python 2.0
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
m = [[x * y for x in row] for row in rows]
