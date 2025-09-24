ConfigMap example for a Spring Boot + Java project connecting to Azure Cosmos DB running in Kubernetes.

I’ll give you a complete example covering:
    The Kubernetes ConfigMap with Cosmos DB endpoint & database name
    The Kubernetes Secret for the Cosmos DB key (don’t store secrets in ConfigMaps!)
    The Spring Boot config to load these values automatically
    The Java CosmosConfig class to wire it all up

1. ConfigMap (non-sensitive settings)
    Create cosmos-config.yaml:
2. Secret (for keys/passwords)
    Create cosmos-secret.yaml (values must be Base64 encoded):
3. Deployment using env vars
    In your Spring Boot deployment deployment.yaml:
4. Spring Boot application.properties
    Use Spring’s @Value or @ConfigurationProperties to inject environment variables:
5. Cosmos DB Configuration Class
    Use Azure Cosmos Spring SDK (spring-boot-starter-data-cosmos):
6. Test locally with kubectl
    kubectl apply -f cosmos-config.yaml
    kubectl apply -f cosmos-secret.yaml
    kubectl apply -f deployment.yaml

Check values:
    kubectl get configmap cosmos-config -o yaml
    kubectl get secret cosmos-secret -o yaml


If you want, I can also give you:
    Helm chart templates for Cosmos ConfigMap + Secret, or
    A Key Vault CSI driver example to pull Cosmos secrets securely without storing them in K8s directly.
