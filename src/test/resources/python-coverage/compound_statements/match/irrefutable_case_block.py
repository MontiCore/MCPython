# Reference: https://docs.python.org/3.15/reference/compound_stmts.html#irrefutable-case-blocks
# Grammar:   case_block
# PEP:       634
# Since:     Python 3.10
# Generated: Claude Opus 5.5 (claude-opus-5-5), 2026-11-20
match x:
    case 1:
        pass
    case (0 | _):
        pass
