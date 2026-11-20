# Reference: https://docs.python.org/3.15/reference/expressions.html#asynchronous-comprehensions
# Grammar:   for_if_clause
# PEP:       530
# Since:     Python 3.11
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
async def f():
    return [[x async for x in aiter(row)] for row in rows]
