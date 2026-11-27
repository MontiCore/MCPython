# Reference: https://docs.python.org/3.15/reference/expressions.html#complex-comprehensions
# Grammar:   for_if_clauses
# PEP:       202
# Since:     Python 2.0
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
names = [
    name
    for module in [array, math]
    for name in vars(module)
    if name.startswith('a')
]
