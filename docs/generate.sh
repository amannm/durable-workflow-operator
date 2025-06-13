#!/usr/bin/env bash
set -eu

pkl eval package://pkg.pkl-lang.org/pkl-pantry/org.json_schema.contrib@1.1.0#/generate.pkl -m . -p source="serverlessworkflow.json"
mv Serverlessworkflow.pkl ServerlessWorkflow.pkl
java -cp /Users/amannmalik/.m2/repository/org/pkl-lang/pkl-tools/0.28.2/pkl-tools-0.28.2.jar \
     org.pkl.codegen.java.Main \
    -o generated \
    ServerlessWorkflow.pkl
