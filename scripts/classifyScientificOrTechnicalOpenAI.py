systemPrompt = """I will give you a fully qualified name and a snippet of python source code. Classify the snippet and decide whether it is \"scientific code\" i.e. code fulfilling a science research goal (like calculating a formula etc), or whether it is \"technical code\" i.e. code fulfilling a task not directly related to a research objective. You only answer with the phrase \"SCIENTIFIC\" or the phrase \"TECHNICAL\""""

import os
import sys
import tiktoken
from tqdm import tqdm
from litellm import completion
from openai.error import OpenAIError

def get_classification(messages, model, **kwargs):
  """
  Call the model (via LiteLLM) to classify into:
  SCIENTIFIC / TECHNICAL / MIXED / NULLCODE / RCODE.
  Returns the classification string.
  """
  try:
    resp = completion(
      model=model,
      messages=messages,
      **kwargs
    )
  except OpenAIError as e:
    print("Error in LLM call:", e)
    raise

  text = resp.choices[0].message.content.strip().upper()

  # Robust category matching
  if "SCIENTIFIC" in text:
    return "SCIENTIFIC"
  elif "TECHNICAL" in text:
    return "TECHNICAL"
  elif "MIXED" in text:
    return "MIXED"
  elif "NULLCODE" in text:
    return "NULLCODE"
  elif "RCODE" in text:
    return "RCODE"
  else:
    print("Unexpected classification:", text)
    return "UNKOWN"  # Fallback category

def build_messages(file_content, system_prompt):
  return [
    {"role": "system", "content": system_prompt},
    {"role": "user",   "content": file_content}
  ]

def main():
  args = sys.argv[1:]
  if len(args) == 0:
    print("Usage: python <script.py> <dir containing 1 file per snippet>")
    sys.exit(1)

  snippets_dir = args[0]

  snippet_paths = [
    os.path.join(root, fname)
    for root, dirs, files in os.walk(snippets_dir)
    for fname in files
  ]

  snippet_fqns = []
  all_messages = []

  for path in snippet_paths:
    with open(path, encoding="utf8") as f:
      content = f.read()
    first_line = content.splitlines()[0] if content.splitlines() else ""
    if first_line.startswith("FQN: "):
      fqn = first_line[len("FQN: "):]
    else:
      fqn = path
    snippet_fqns.append(fqn)

    msgs = build_messages(content, system_prompt)
    all_messages.append(msgs)

  print("Will classify", len(all_messages), "snippets")

  # Estimate token usage
  tokenizer = tiktoken.get_encoding("cl100k_base")
  flattened = [m["content"] for msgs in all_messages for m in msgs]
  token_seqs = [tokenizer.encode(s) for s in flattened]
  total_tokens = sum(len(seq) for seq in token_seqs)
  print("Overall sending around", total_tokens, "tokens")

  resp = input("Do you want to continue? (y/n): ")
  if resp.lower() != "y":
    sys.exit(0)

  os.makedirs("target", exist_ok=True)
  with open("target/results.txt", "a", encoding="utf8") as res_file:
    for msgs, fqn in tqdm(zip(all_messages, snippet_fqns), total=len(all_messages)):
      print("Processing", fqn)
      c = get_classification(
        msgs,
        model="gpt-3.5-turbo",
        temperature=0,
        max_tokens=1600,
        top_p=0.95
      )
      print(" ->", c)

      # one-hot encoding for all categories
      scientific = 1.0 if c == "SCIENTIFIC" else 0.0
      technical  = 1.0 if c == "TECHNICAL" else 0.0
      mixed      = 1.0 if c == "MIXED" else 0.0
      nullcode   = 1.0 if c == "NULLCODE" else 0.0
      rcode      = 1.0 if c == "RCODE" else 0.0

      res_line = (
          '{"fqn": "' + fqn + '", '
          + '"type": "method", '
          + f'"scientific": {scientific}, '
          + f'"technical": {technical}, '
          + f'"mixed": {mixed}, '
          + f'"nullcode": {nullcode}, '
          + f'"rcode": {rcode}'
          + "},\n"
      )
      res_file.write(res_line)


if __name__ == "__main__":
  main()
