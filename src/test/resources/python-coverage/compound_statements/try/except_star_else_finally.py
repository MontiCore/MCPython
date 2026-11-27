# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#except-star
# Grammar:   try_stmt
# PEP:       654
# Since:     Python 3.11
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
try:
    f()
except* ValueError:
    pass
except* TypeError:
    pass
else:
    pass
finally:
    pass
