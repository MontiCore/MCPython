# Reference: https://docs.python.org/3.15/reference/simple_stmts.html#the-type-statement
# Grammar:   type_param_starred_default
# PEP:       696
# Since:     Python 3.13
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
type Row[*Ts = *tuple[int, str]] = tuple[*Ts]
