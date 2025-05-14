# How to classify a python project

1. Build jar: `gradle build`
2. Create snippets from project: `java -cp target/libs/sipython-7.8.0-SNAPSHOT-cli.jar de.monticore.python.SplitIntoFunctionsUtil <pythonProjectDir> <snippetDir>`
3. Classify snippets: `python ./scripts/classifyScientificOrTechnicalOpenAI.py <snippetDir>`
4. The results are saved under `target/results.txt`
