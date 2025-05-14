import openai
import os
import sys
import tiktoken
from tqdm import tqdm

args = sys.argv
args.pop(0)

if len(args) == 0:
    print("Usage: python <script.py> <dir containing 1 file per snippet>")
    exit(1)

client = openai.OpenAI(
    base_url = "http://localhost:11434/v1",
    api_key = "ollama"
)

def getClassification(messages):
    answer = client.chat.completions.create(
        model="qwen3:14b",
        messages=messages,
        temperature=0,
        max_tokens=1600,
        top_p=0.95,
        frequency_penalty=0,
        presence_penalty=0,
        stop=None,
        stream=False
    )

    res = answer.choices[0].message.content
    print(res)
    # TODO: improve since thinking models might use SCIENTIFIC in thinking block
    if("SCIENTIFIC" in res):
        return "SCIENTIFIC"
    elif "TECHNICAL" in res:
        return "TECHNICAL"
    elif "UNKOWN" in res:
        return "UNKOWN"
    else:
        print("Error while classifying. Got answer " + res)

def buildMessages(fileContent):
    return [
        {
            "role": "system",
            "content": """I will give you a fully qualified name and a snippet of python source code. Classify the snippet and decide whether it is \"scientific code\" i.e. code fulfilling a science research goal (like calculating a formula etc), or whether it is \"technical code\" i.e. code fulfilling a task not directly related to a research objective. You only answer with the phrase \"SCIENTIFIC\" or the phrase \"TECHNICAL\""""
        },
        {
            "role": "user",
            "content": fileContent
        }
    ]


snippet_files = [os.path.join(root, file) for root, dirs, files in os.walk(args[0]) for file in files]
snippet_fqn = []


allMessages = []

for sfPath in snippet_files:
    with open(sfPath, encoding="utf8") as f:
        content = f.read()
        snippet_fqn.append(content.splitlines()[0][len("FQN: "):])
        allMessages.append(buildMessages(content))


# TODO: tmp: only a few
allMessages = allMessages[10:20]
snippet_fqn = snippet_fqn[10:20]
# print("TMP: Only classifying 3 snippets for experimentation")
print("Will classify ", len(allMessages), "snippets")

#for msgs in allMessages:
#    print("#"* 20)
#    for msg in msgs:
#        print(msg["role"])
#        print(msg["content"])
#        print()

# estimate costs
tokenizer = tiktoken.get_encoding("cl100k_base")

flattenedMsgContents = [msg["content"] for msgs in allMessages for msg in msgs]
tokens = [tokenizer.encode(it) for it in flattenedMsgContents]
numberOfTokens = sum(len(it) for it in tokens)

print("Overall sending", numberOfTokens, "tokens")
print("This will cost around", numberOfTokens / 1_000_000, "$") # rough estimate 1M token = 1$
while True:
    user_input = input("Do you want to continue? (y/n): ")
    if user_input.lower() == "y":
        break
    else:
        exit(1)

with open("target/results.txt", "a") as resFile:
    for msgs, sf in tqdm(zip(allMessages, snippet_fqn)):
        print("Processing ", sf)
        c = getClassification(msgs)
        # c = "SCIENTIFIC"
        print(c)
        scientific = 1.0 if c == "SCIENTIFIC" else 0.0
        technical = 1.0 if c == "TECHNICAL" else 0.0
        nullcode = 1 - scientific - technical

        resFile.write( '{"fqn": "' + sf + '", "type": "method", "scientific": ' + str(scientific) + ', "technical": ' + str(technical) + ', "nullcode": ' + str(nullcode) + "}," + "\n")
