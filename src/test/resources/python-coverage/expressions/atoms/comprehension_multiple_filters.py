# Reference: https://docs.python.org/3.15/reference/expressions.html#filtering-in-comprehensions
# Grammar:   for_if_clause
# PEP:       202
# Since:     Python 2.0
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
xs = [x for x in xs if x > 0 if x % 2]
