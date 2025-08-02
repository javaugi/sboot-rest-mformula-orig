I have an azure account and github secretes setup already done. I'd like to deploy the github app to Azure using CI/CD Azure with Kubernetes and 
    GitHub Actions and Cosmos DB. Could you give me step-to-step guide to ceate resources, ci/cd pileline to deploy the github app to Azure?

ACR - Azure Container Registry

That's an excellent goal! Deploying a GitHub app (which I'll assume is a Spring Boot Java application as per our previous discussion) to Azure 
    Kubernetes Service (AKS) with a Cosmos DB backend, all orchestrated by GitHub Actions, is a solid cloud-native setup.

Here's a step-by-step guide covering resource creation, application configuration, and the CI/CD pipeline.

Prerequisites - Before we start, ensure you have:

Azure Account and Subscription: You confirmed you have this.
Azure CLI Installed: Make sure it's installed and logged in (az login).
kubectl Installed: For interacting with Kubernetes.
GitHub Repository: Your Java application code should be in a GitHub repository.
GitHub Secrets: You confirmed these are set up. We'll use them to authenticate with Azure.

AZURE_CLIENT_ID
AZURE_CLIENT_SECRET
AZURE_TENANT_ID
AZURE_SUBSCRIPTION_ID
ACR_USERNAME (optional, can use service principal for ACR login)
ACR_PASSWORD (optional, can use service principal for ACR login)

If you haven't created a Service Principal for GitHub Actions, do it now:


az ad sp create-for-rbac --name "github-actions-aks-sp" --role contributor --scopes /subscriptions/<YOUR_SUBSCRIPTION_ID> --sdk-auth

This will output JSON with clientId, clientSecret, tenantId, and subscriptionId. Use these for your GitHub secrets. For ACR_USERNAME and ACR_PASSWORD, you can enable the admin user on your ACR and use those credentials, or preferably, use the same Service Principal for authentication to ACR.

Step 1: Create Azure Resources
We'll use Azure CLI to create the necessary infrastructure.

1. Define Environment Variables (Local CLI)

Set these for easy command execution.

Bash

RESOURCE_GROUP_NAME="cvs-pharmacy-rg"
LOCATION="eastus" # Choose a region close to you
AKS_CLUSTER_NAME="cvs-pharmacy-aks"
ACR_NAME="cvspharmacyacr$(head /dev/urandom | tr -dc a-z0-9 | head -c 5)" # Unique name
COSMOSDB_ACCOUNT_NAME="cvspharmacycosmosdb$(head /dev/urandom | tr -dc a-z0-9 | head -c 5)" # Unique name
COSMOSDB_DATABASE_NAME="PharmacyDB"
COSMOSDB_CONTAINER_NAME="Prescriptions"

2. Create a Resource Group

Bash

az group create --name $RESOURCE_GROUP_NAME --location $LOCATION
3. Create Azure Container Registry (ACR)

ACR will store your Docker images.

Bash

az acr create --resource-group $RESOURCE_GROUP_NAME \
    --name $ACR_NAME \
    --sku Basic \
    --admin-enabled # Enable admin user for simpler GitHub Actions setup, though Service Principal is more secure long-term
4. Create Azure Kubernetes Service (AKS) Cluster

Bash

az aks create --resource-group $RESOURCE_GROUP_NAME \
    --name $AKS_CLUSTER_NAME \
    --node-count 2 \
    --enable-managed-identity \
    --attach-acr $ACR_NAME \
    --generate-ssh-keys # Generates SSH keys if you don't have them
--node-count: Number of worker nodes. Start with 2, scale up later.

--enable-managed-identity: Recommended for secure access to other Azure services from AKS pods.

--attach-acr: Directly integrates AKS with your ACR, making image pulls easier and more secure.

5. Create Azure Cosmos DB Account (SQL API)

Bash

az cosmosdb create --name $COSMOSDB_ACCOUNT_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --locations RegionName=$LOCATION failoverPriority=0 \
    --default-consistency-level Session \
    --kind GlobalDocumentDB # For SQL (Core) API
6. Create a Cosmos DB Database and Container

Bash

az cosmosdb sql database create --account-name $COSMOSDB_ACCOUNT_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --name $COSMOSDB_DATABASE_NAME

az cosmosdb sql container create --account-name $COSMOSDB_ACCOUNT_NAME \
    --resource-group $RESOURCE_GROUP_NAME \
    --database-name $COSMOSDB_DATABASE_NAME \
    --name $COSMOSDB_CONTAINER_NAME \
    --partition-key-path /orderId \
    --throughput 400 # Or --max-throughput 1000 for autoscale
--partition-key-path: Crucial for Cosmos DB performance. /orderId is a good example if orderId is frequently used for lookups. Choose wisely based on your application's access patterns.

7. Get Cosmos DB Connection String

You'll need this for your application. Store it securely in Kubernetes secrets, not directly in code or environment variables in your deployment manifest.

Bash

COSMOSDB_CONNECTION_STRING=$(az cosmosdb show-connection-string --name $COSMOSDB_ACCOUNT_NAME --resource-group $RESOURCE_GROUP_NAME --query connectionStrings[0].connectionString --output tsv)
echo "Cosmos DB Connection String: $COSMOSDB_CONNECTION_STRING"
Keep this connection string private! We'll use it to create a Kubernetes secret.

Step 2: Configure Your Java Application
Your Spring Boot application needs to connect to Cosmos DB.

1. Add Dependencies (pom.xml)

XML

<dependency>
    <groupId>com.azure.spring</groupId>
    <artifactId>spring-cloud-azure-starter-data-cosmos</artifactId>
</groupId>
</dependency>
<dependency>
    <groupId>com.azure.spring</groupId>
    <artifactId>spring-cloud-azure-starter-keyvault-secrets</artifactId>
</dependency>

Note: Make sure your spring-cloud-azure-dependencies BOM is set to a compatible version in your dependencyManagement section, e.g., 5.x.x.

2. Application Properties (application.properties or application.yml)

This configuration will usually be overridden by environment variables injected into the Kubernetes Pod.

Properties

# Basic Spring Boot setup
server.port=8080

# Azure Cosmos DB Configuration (placeholder, values injected by Kubernetes Secret)
azure.cosmos.uri=${AZURE_COSMOS_URI}
azure.cosmos.key=${AZURE_COSMOS_KEY}
azure.cosmos.database=${AZURE_COSMOS_DATABASE}
# Optional: Set preferred regions for multi-region Cosmos DB
# azure.cosmos.preferred-regions=EastUS,WestUS

# Enable Spring Boot Actuator for health checks (for Kubernetes probes)
management.endpoints.web.exposure.include=health,info,liveness,readiness
management.endpoint.health.show-details=always
3. Example Cosmos DB Repository (Optional but recommended)

Java

// src/main/java/com/cvs/pharmacy/repository/PrescriptionRepository.java
package com.cvs.pharmacy.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.cvs.pharmacy.model.PrescriptionOrder; // Your PrescriptionOrder model
import org.springframework.stereotype.Repository;

// Assumes PrescriptionOrder has an 'id' field annotated with @Id or equivalent
@Repository
public interface PrescriptionRepository extends CosmosRepository<PrescriptionOrder, String> {
    // Custom query methods if needed, e.g.,
    // List<PrescriptionOrder> findByPatientId(String patientId);
}
Java

// src/main/java/com/cvs/pharmacy/model/PrescriptionOrder.java
package com.cvs.pharmacy.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Objects;

@Container(containerName = "Prescriptions") // Matches your Cosmos DB container name
public class PrescriptionOrder {
    @Id
    private String id; // This will be mapped to the Cosmos DB 'id' field
    @PartitionKey
    private String orderId; // Matches your Cosmos DB partition key path
    private String patientId;
    private String drugName;
    private int quantity;
    private Instant orderDate; // Use Instant for timestamps

    public PrescriptionOrder() {}

    public PrescriptionOrder(String id, String orderId, String patientId, String drugName, int quantity, Instant orderDate) {
        this.id = id;
        this.orderId = orderId;
        this.patientId = patientId;
        this.drugName = drugName;
        this.quantity = quantity;
        this.orderDate = orderDate;
    }

    // Getters and Setters (or use Lombok)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDrugName() { return drugName; }
    public void setDrugName(String drugName) { this.drugName = drugName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Instant getOrderDate() { return orderDate; }
    public void setOrderDate(Instant orderDate) { this.orderDate = orderDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionOrder that = (PrescriptionOrder) o;
        return quantity == that.quantity && Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(patientId, that.patientId) && Objects.equals(drugName, that.drugName) && Objects.equals(orderDate, that.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, patientId, drugName, quantity, orderDate);
    }

    @Override
    public String toString() {
        return "PrescriptionOrder{" +
               "id='" + id + '\'' +
               ", orderId='" + orderId + '\'' +
               ", patientId='" + patientId + '\'' +
               ", drugName='" + drugName + '\'' +
               ", quantity=" + quantity +
               ", orderDate=" + orderDate +
               '}';
    }
}

// src/main/java/com/cvs/pharmacy/controller/PrescriptionController.java (Example REST controller)
package com.cvs.pharmacy.controller;

import com.cvs.pharmacy.model.PrescriptionOrder;
import com.cvs.pharmacy.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/prescriptions")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionController(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    @PostMapping
    public ResponseEntity<PrescriptionOrder> createPrescription(@RequestBody PrescriptionOrder order) {
        if (order.getId() == null || order.getId().isEmpty()) {
            order.setId(UUID.randomUUID().toString()); // Generate a unique ID if not provided
        }
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            order.setOrderId(order.getId()); // Use ID as orderId if not explicitly set for partition key demo
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(Instant.now());
        }
        PrescriptionOrder savedOrder = prescriptionRepository.save(order);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionOrder> getPrescriptionById(@PathVariable String id) {
        Optional<PrescriptionOrder> order = prescriptionRepository.findById(id);
        return order.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionOrder>> getAllPrescriptions() {
        List<PrescriptionOrder> orders = (List<PrescriptionOrder>) prescriptionRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    // You might also have methods for updating, deleting, or custom queries
}
Step 3: Kubernetes Manifests
Create a k8s directory in your project root to store these files.

1. Create a Kubernetes Secret for Cosmos DB Connection

This must not be committed to GitHub. Create it manually or via a separate, secure process (e.g., Azure Key Vault integration with AKS, or inject via pipeline if your security policy allows).

Bash

# Extract URI and Key from the connection string for separate environment variables
# Example: "AccountEndpoint=https://YOUR_COSMOS_NAME.documents.azure.com:443/;AccountKey=YOUR_COSMOS_KEY==;"
COSMOS_URI=$(echo "$COSMOSDB_CONNECTION_STRING" | sed -n 's/.*AccountEndpoint=\([^;]*\);.*/\1/p')
COSMOS_KEY=$(echo "$COSMOSDB_CONNECTION_STRING" | sed -n 's/.*AccountKey=\([^;]*\);.*/\1/p')

kubectl create secret generic cosmosdb-credentials \
    --from-literal=AZURE_COSMOS_URI=$COSMOS_URI \
    --from-literal=AZURE_COSMOS_KEY=$COSMOS_KEY \
    --from-literal=AZURE_COSMOS_DATABASE=$COSMOSDB_DATABASE_NAME \
    --namespace default # Or your application's namespace
2. k8s/deployment.yaml

YAML

apiVersion: apps/v1
kind: Deployment
metadata:
  name: cvs-pharmacy-app
  labels:
    app: cvs-pharmacy-app
spec:
  replicas: 2 # Start with 2 replicas for high availability
  selector:
    matchLabels:
      app: cvs-pharmacy-app
  template:
    metadata:
      labels:
        app: cvs-pharmacy-app
    spec:
      containers:
      - name: cvs-pharmacy-app
        image: <YOUR_ACR_LOGIN_SERVER>/cvs-pharmacy-app:latest # Placeholder for ACR image
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: prod
        - name: AZURE_COSMOS_URI
          valueFrom:
            secretKeyRef:
              name: cosmosdb-credentials
              key: AZURE_COSMOS_URI
        - name: AZURE_COSMOS_KEY
          valueFrom:
            secretKeyRef:
              name: cosmosdb-credentials
              key: AZURE_COSMOS_KEY
        - name: AZURE_COSMOS_DATABASE
          valueFrom:
            secretKeyRef:
              name: cosmosdb-credentials
              key: AZURE_COSMOS_DATABASE
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 90
          periodSeconds: 15
          timeoutSeconds: 5
          failureThreshold: 3
      imagePullSecrets:
      - name: acr-secret # This secret is automatically created by AKS when `--attach-acr` is used or manually for non-attached ACR.
3. k8s/service.yaml

YAML

apiVersion: v1
kind: Service
metadata:
  name: cvs-pharmacy-service
spec:
  selector:
    app: cvs-pharmacy-app
  ports:
    - protocol: TCP
      port: 80 # Service port
      targetPort: 8080 # Container port
  type: LoadBalancer # Expose externally via Azure Load Balancer. For production, consider Ingress Controller.
type: LoadBalancer will provision a public IP and an Azure Load Balancer. For production, an Ingress Controller (like NGINX Ingress or Azure Application Gateway Ingress Controller) is usually preferred for advanced routing, TLS termination, and WAF capabilities.

Step 4: Dockerfile
Create a Dockerfile in your project root.

Dockerfile

# Use a slim Java base image for smaller size and better security
FROM eclipse-temurin:17-jre-alpine

# Set working directory in the container
WORKDIR /app

# Copy the JAR file (assuming your Maven build produces a JAR in target/)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose the port your Spring Boot app listens on (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
Step 5: GitHub Actions CI/CD Workflow
Create .github/workflows/main.yml in your GitHub repository.

YAML

name: Deploy CVS Pharmacy App to AKS

on:
  push:
    branches:
      - main # Trigger on pushes to the main branch
  pull_request:
    branches:
      - main # Trigger on pull requests to the main branch

env:
  AZURE_CONTAINER_REGISTRY: ${{ secrets.ACR_NAME }} # Use a GitHub Secret for ACR name if you prefer
  RESOURCE_GROUP: cvs-pharmacy-rg                 # Your Azure Resource Group
  AKS_CLUSTER_NAME: cvs-pharmacy-aks              # Your AKS cluster name
  IMAGE_NAME: cvs-pharmacy-app
  JAVA_VERSION: '17'
  APP_PORT: 8080 # Port your Spring Boot app listens on

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    environment: production # Optional: Define an environment for approval flows

    steps:
    - name: Checkout code
      uses: actions/checkout@v4

    - name: Set up JDK ${{ env.JAVA_VERSION }}
      uses: actions/setup-java@v4
      with:
        java-version: ${{ env.JAVA_VERSION }}
        distribution: 'temurin'
        cache: 'maven'

    - name: Build with Maven
      run: mvn clean package -DskipTests # `package` creates the JAR
      # If you have extensive unit tests, run them separately and ensure success
      # - name: Run Unit Tests
      #   run: mvn test

    - name: Azure Login
      uses: azure/login@v2
      with:
        client-id: ${{ secrets.AZURE_CLIENT_ID }}
        tenant-id: ${{ secrets.AZURE_TENANT_ID }}
        subscription-id: ${{ secrets.AZURE_SUBSCRIPTION_ID }}

    - name: Log in to Azure Container Registry
      uses: azure/docker-login@v2
      with:
        login-server: ${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io
        # Using Service Principal for ACR login is more robust
        username: ${{ secrets.AZURE_CLIENT_ID }}
        password: ${{ secrets.AZURE_CLIENT_SECRET }}

    - name: Build and Push Docker image
      run: |
        docker build . --file Dockerfile --tag ${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io/${{ env.IMAGE_NAME }}:${{ github.sha }}
        docker push ${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io/${{ env.IMAGE_NAME }}:${{ github.sha }}
        # Also tag with 'latest' for convenience, but use SHA for immutable deployments in K8s
        docker tag ${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io/${{ env.IMAGE_NAME }}:${{ github.sha }} ${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io/${{ env.IMAGE_NAME }}:latest
        docker push ${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io/${{ env.IMAGE_NAME }}:latest

    - name: Set up kubectl context for AKS
      uses: azure/aks-set-context@v3
      with:
        resource-group: ${{ env.RESOURCE_GROUP }}
        cluster-name: ${{ env.AKS_CLUSTER_NAME }}
        # Kubeconfig is automatically handled by the action using the Azure login

    - name: Deploy to AKS
      run: |
        # Use sed to update the image tag in deployment.yaml
        # This is a simple approach. For production, consider `kustomize` or `helm`
        # for more robust templating and release management.
        IMAGE_FULL_TAG="${{ env.AZURE_CONTAINER_REGISTRY }}.azurecr.io/${{ env.IMAGE_NAME }}:${{ github.sha }}"
        sed -i "s|<YOUR_ACR_LOGIN_SERVER>/cvs-pharmacy-app:latest|$IMAGE_FULL_TAG|g" k8s/deployment.yaml

        kubectl apply -f k8s/deployment.yaml
        kubectl apply -f k8s/service.yaml

    - name: Verify Deployment (Optional)
      run: |
        echo "Waiting for deployment to be ready..."
        kubectl rollout status deployment/cvs-pharmacy-app --timeout=300s
        kubectl get pods -l app=${{ env.IMAGE_NAME }}
        kubectl get services cvs-pharmacy-service

    # - name: Run Post-Deployment Tests (Optional)
    #   run: |
    #     # Example: If your service exposes a health endpoint
    #     SERVICE_IP=$(kubectl get service cvs-pharmacy-service -o jsonpath='{.status.loadBalancer.ingress[0].ip}')
    #     echo "Service IP: $SERVICE_IP"
    #     # Give some time for the LoadBalancer to get an IP
    #     sleep 60
    #     curl -v http://${SERVICE_IP}/actuator/health
Important Considerations and Next Steps:
Security:

Managed Identities (MSI): While we inject Cosmos DB secrets via Kubernetes secrets, a more advanced and secure approach is to use Azure Managed Identities for your AKS Pods. You can assign the Pod's identity permissions to access Cosmos DB (and Key Vault if you store secrets there), removing the need to pass connection strings as environment variables.

Azure Key Vault: Store all sensitive application secrets (like Cosmos DB connection strings) in Azure Key Vault. Use Azure Key Vault Provider for CSI Secrets Store to mount these secrets directly into your AKS pods as files. This is the recommended production approach.

Network Security: Implement Azure Private Link for Cosmos DB to ensure traffic between AKS and Cosmos DB remains entirely within Azure's private network, never traversing the public internet. This significantly enhances security.

Role-Based Access Control (RBAC): Ensure your Service Principal for GitHub Actions has the least privilege necessary. Contributor on the Resource Group is broad; refine it to specific roles like AcrPush for ACR and Kubernetes Cluster Admin for AKS.

Robust Deployments:

Helm Charts: For managing Kubernetes applications, Helm is the industry standard. It provides templating, versioning, and release management. Instead of kubectl apply -f, you'd use helm upgrade --install.

Kustomize: Another tool for customizing Kubernetes manifests without templating.

Blue/Green, Canary Deployments: For zero-downtime deployments and risk reduction, investigate advanced deployment strategies. These are often implemented with an Ingress Controller (like NGINX or Application Gateway Ingress) and possibly service mesh technologies (like Linkerd or Istio).

Observability:

Integrate Azure Monitor and Application Insights into your Spring Boot application. Spring Cloud Azure provides starters for this. This gives you rich metrics, logs, and distributed tracing.

Set up alerts in Azure Monitor for application errors, high resource utilization, and other critical events.

Scaling:

Configure Horizontal Pod Autoscalers (HPA) for your AKS deployments to automatically scale pods based on CPU/memory utilization or custom metrics.

Configure Cluster Autoscaler for AKS to automatically scale the number of nodes in your cluster based on pending pods.

Cost Management:

Monitor your Azure costs regularly.

Right-size your AKS nodes and Cosmos DB throughput (consider autoscale for Cosmos DB).

Utilize Azure Advisor for cost-saving recommendations.

GitHub Actions Best Practices:

Environments: Use GitHub Actions Environments for defining different deployment stages (e.g., dev, staging, production) with approval gates and specific secrets for each.

Reusable Workflows: Break down complex workflows into reusable components for cleaner, more maintainable pipelines.

This guide provides a strong foundation. As you move to a Staff-level role, the expectation shifts from just "making it work" to designing for scale, resilience, security, and maintainability
