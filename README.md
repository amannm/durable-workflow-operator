# durable-workflow-operator

This project combines the following technologies:

- [Serverless Workflow specification](https://github.com/serverlessworkflow/specification)
- [Restate](https://github.com/restatedev/restate)
- [Java Operator SDK](https://github.com/operator-framework/java-operator-sdk)

into a system that accepts a workflow definition, deploys it on Kubernetes, starts executing the tasks while it uses the journaling capabilities of Restate to keep track of everything.

## Building

This is a standard Maven project. To build it, run:

```bash
mvn package
```

This will download all dependencies and create a runnable jar in the `target` directory.


## Deploying

You can apply the raw Kubernetes manifests from the `deploy` directory:

```bash
kubectl apply -f deploy/
```

A Helm chart is provided under `charts/durable-workflow-operator`:

```bash
helm install workflow-operator charts/durable-workflow-operator
```
