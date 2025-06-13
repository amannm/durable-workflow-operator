# durable-workflow-operator

this project combines the following technologies:

* https://github.com/serverlessworkflow/specification
* https://github.com/restatedev/restate
* https://github.com/operator-framework/java-operator-sdk

into a system that accepts a workflow definition, deploys it on kubernetes, starts executing the tasks while it uses the journaling capabilities of restate to keep track of everything
