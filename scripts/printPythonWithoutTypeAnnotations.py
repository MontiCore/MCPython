import ast
import os
import sys

class RemoveTypeHints(ast.NodeTransformer):
    def visit_AnnAssign(self, node: ast.AnnAssign):
        if node.value != None:
            assign_node = ast.Assign(targets=[node.target], value=node.value, lineno=node.lineno, end_lineno=node.end_lineno)
            assign_node._fields = node._fields
            return assign_node
        else:
            return node

    def visit_FunctionDef(self, node):
        node.returns = None
        self.generic_visit(node)
        return node

    def visit_arg(self, node):
        node.annotation = None
        return node

    def visit_ClassDef(self, node):
        new_decorators = []
        for decorator in node.decorator_list:
            print(ast.dump(decorator))
            if "dataclass" in  ast.dump(decorator):
                continue
            new_decorators.append(decorator)

        if(len(node.decorator_list) != 0):
            print("Replacing decorators", node.decorator_list, "by", new_decorators)

        node.decorator_list = new_decorators
        self.generic_visit(node)
        return node


def remove_type_hints_in_directory(input_directory, output_directory):
    for root, dirs, files in os.walk(input_directory):
        for file_name in files:
            if file_name.endswith(".py"):
                input_file_path = os.path.join(root, file_name)
                print("Parsing file://" + input_file_path)

                with open(input_file_path, 'r', encoding='utf8') as file:
                    code = file.read()
                    if len(code) > 0 and code[0] == "\uFEFF":
                        code = code[1:]

                try:
                    # Parse the code into AST
                    parsed_code = ast.parse(code)

                    # Remove type hints using the custom visitor
                    remover = RemoveTypeHints()
                    code_without_type_hints = remover.visit(parsed_code)

                    # Create output directory structure if it doesn't exist
                    output_file_path = os.path.join(output_directory, os.path.relpath(input_file_path, input_directory))
                    os.makedirs(os.path.dirname(output_file_path), exist_ok=True)

                    print("Writing file://" + output_file_path)
                    # Save the modified code in the output file
                    with open(output_file_path, 'w', encoding='utf8') as output_file:
                        output_file.write(ast.unparse(code_without_type_hints))
                except SyntaxError as e:
                    print(f"Error parsing the file '{input_file_path}': {e}")


if len(sys.argv) != 3:
    print("Removes type annotations from python code and writes the result to a new dir")
    print("Usage:", "python", sys.argv[0], "<src dir> <target dir>")
    exit(1)

remove_type_hints_in_directory(sys.argv[1], sys.argv[2])
