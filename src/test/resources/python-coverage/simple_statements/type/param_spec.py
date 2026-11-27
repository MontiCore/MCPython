# Reference: https://docs.python.org/3.15/reference/simple_stmts.html#the-type-statement
# Grammar:   type_param
# PEP:       695
# Since:     Python 3.12
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
type Handler[**P] = Callable[P, None]
