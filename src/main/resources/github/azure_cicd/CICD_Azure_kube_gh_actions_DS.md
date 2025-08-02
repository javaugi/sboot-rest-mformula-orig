Here’s a step-by-step guide to deploy a Java/Spring Boot app to Azure Kubernetes Service (AKS) with Cosmos DB, using GitHub Actions CI/CD.

ACR - Azure Container Registry

Step 1: Set Up Azure Resources
1.1. Create Azure Resources
Run these commands in Azure CLI (or use the Azure Portal):

# Login to Azure
az login

# Create a Resource Group
az group create --name PharmaRG --location eastus

# Create AKS Cluster (adjust node count as needed)
az aks create --resource-group PharmaRG --name PharmaAKS --node-count 2 --enable-addons monitoring --generate-ssh-keys

# Create Azure Container Registry (ACR)
az acr create --resource-group PharmaRG --name PharmaACR --sku Basic

# Attach ACR to AKS
az aks update --resource-group PharmaRG --name PharmaAKS --attach-acr PharmaACR

# Create Cosmos DB (SQL API)
az cosmosdb create --name PharmaCosmosDB --resource-group PharmaRG --kind GlobalDocumentDB --locations regionName=eastus
az cosmosdb sql database create --account-name PharmaCosmosDB --name PrescriptionDB --resource-group PharmaRG
az cosmosdb sql container create --account-name PharmaCosmosDB --database-name PrescriptionDB --name Prescriptions --partition-key-path "/patientId" --resource-group PharmaRG

1.2. Get Connection Strings
# Get AKS credentials
az aks get-credentials --resource-group PharmaRG --name PharmaAKS

# Get Cosmos DB connection string
az cosmosdb keys list --name PharmaCosmosDB --resource-group PharmaRG --type connection-strings
Save the Cosmos DB connection string for later.

Step 2: Set Up the Spring Boot App
2.1. Sample Spring Boot + Cosmos DB App
pom.xml (Dependencies)

xml
<dependencies>
    <dependency>
        <groupId>com.azure</groupId>
        <artifactId>azure-cosmos</artifactId>
        <version>4.45.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
application.properties

properties
# Cosmos DB Config
azure.cosmos.uri=${COSMOS_URI}
azure.cosmos.key=${COSMOS_KEY}
azure.cosmos.database=PrescriptionDB
azure.cosmos.container=Prescriptions
Prescription.java (Entity)

java
public class Prescription {
    private String id;
    private String patientId;
    private String drugName;
    // Getters & Setters
}
PrescriptionController.java

java
@RestController
public class PrescriptionController {
    @Autowired
    private CosmosClient cosmosClient;

    @PostMapping("/prescriptions")
    public Prescription addPrescription(@RequestBody Prescription prescription) {
        CosmosContainer container = cosmosClient.getDatabase("PrescriptionDB")
                .getContainer("Prescriptions");
        container.createItem(prescription);
        return prescription;
    }
}
Step 3: Dockerize the App
Dockerfile

FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

Step 4: Set Up GitHub Actions CI/CD
4.1. Store Secrets in GitHub

Go to: Repo → Settings → Secrets → Actions
Add these secrets:

AZURE_CREDENTIALS (from az ad sp create-for-rbac --name PharmaGitHubAction --role contributor)
COSMOS_URI (from az cosmosdb keys list)
COSMOS_KEY (from az cosmosdb keys list)

4.2. Create GitHub Workflow
.github/workflows/deploy-aks.yml

name: Deploy to AKS

on:
  push:
    branches: [ "main" ]

env:
  ACR_NAME: pharmaacr.azurecr.io  # Replace with your ACR name
  AKS_CLUSTER: PharmaAKS
  APP_NAME: pharmacy-service

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      # Build with Maven
      - name: Build with Maven
        run: mvn package -DskipTests

      # Login to Azure
      - name: Azure Login
        uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}

      # Build & Push Docker Image
      - name: Build and Push Docker Image
        run: |
          docker build -t $ACR_NAME/$APP_NAME:${{ github.sha }} .
          docker push $ACR_NAME/$APP_NAME:${{ github.sha }}

      # Deploy to AKS
      - name: Deploy to AKS
        run: |
          az aks get-credentials --resource-group PharmaRG --name $AKS_CLUSTER
          kubectl create secret generic cosmos-secret \
            --from-literal=COSMOS_URI=${{ secrets.COSMOS_URI }} \
            --from-literal=COSMOS_KEY=${{ secrets.COSMOS_KEY }}
          kubectl apply -f k8s/
Step 5: Kubernetes Deployment Files

5.1. k8s/deployment.yml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: pharmacy-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: pharmacy-service
  template:
    metadata:
      labels:
        app: pharmacy-service
    spec:
      containers:
      - name: pharmacy-service
        image: pharmaacr.azurecr.io/pharmacy-service:latest
        ports:
        - containerPort: 8080
        envFrom:
        - secretRef:
            name: cosmos-secret
5.2. k8s/service.yml

apiVersion: v1
kind: Service
metadata:
  name: pharmacy-service
spec:
  type: LoadBalancer
  ports:
  - port: 80
    targetPort: 8080
  selector:
    app: pharmacy-service

Step 6: Deploy & Verify
Push to GitHub → Triggers CI/CD.

Check Deployment Status:

kubectl get pods -w  # Watch pods
kubectl get service  # Get external IP

Test API:

curl -X POST http://<EXTERNAL-IP>/prescriptions -H "Content-Type: application/json" -d '{"patientId":"123","drugName":"Aspirin"}'
Final Notes
✅ Auto-Scaling: Configure HorizontalPodAutoscaler in AKS.
✅ Monitoring: Use Azure Monitor + Prometheus.
✅ Security: Enable Azure Key Vault for secrets.

Would you like to add auto-scaling or database backups next? 🚀

yes please add auto-scaling and database backups steps to the pipeline
Here’s how to add auto-scaling (HPA) and Cosmos DB backups to your Azure CI/CD pipeline:

Part 1: Auto-Scaling in AKS (Horizontal Pod Autoscaler - HPA)
1.1 Enable Metrics Server in AKS
bash
az aks enable-addons --resource-group PharmaRG --name PharmaAKS --addons monitoring
1.2 Add HPA Configuration
k8s/hpa.yml

apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: pharmacy-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: pharmacy-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 50

1.3 Update GitHub Actions Workflow
Add this step to deploy-aks.yml after kubectl apply -f k8s/:

- name: Apply HPA
  run: kubectl apply -f k8s/hpa.yml

Part 2: Cosmos DB Automated Backups
2.1 Enable Continuous Backup (Point-in-Time Restore)

az cosmosdb update --name PharmaCosmosDB --resource-group PharmaRG --enable-continuous-backup true

2.2 Create Backup Policy (Azure CLI)

# Create a backup policy JSON file
echo '{
  "continuousModeProperties": {
    "tier": "Continuous30Days"
  }
}' > backup-policy.json

# Apply the policy
az cosmosdb sql backup-policy update \
  --resource-group PharmaRG \
  --account-name PharmaCosmosDB \
  --name PrescriptionDB \
  --policy @backup-policy.json

2.3 Restore from Backup (Example Command)

az cosmosdb sql restorable-database list \
  --account-name PharmaCosmosDB \
  --location eastus

az cosmosdb sql database restore \
  --account-name PharmaCosmosDB \
  --name PrescriptionDB \
  --resource-group PharmaRG \
  --restore-timestamp 2024-05-20T00:00:00Z

Part 3: Update CI/CD Pipeline for Backup Checks
Add a post-deployment step in deploy-aks.yml to verify backups:

- name: Verify Cosmos DB Backup
  run: |
    echo "Checking backup status..."
    az cosmosdb sql backup-policy show \
      --account-name PharmaCosmosDB \
      --resource-group PharmaRG \
      --name PrescriptionDB

Final Architecture

[GitHub Actions] → [ACR] → [AKS (Auto-Scaled Pods)]  
                     │  
                     └── [Cosmos DB (Continuous Backup)]  

Key Commands to Verify
# Check HPA status
kubectl get hpa

# Check backup policy
az cosmosdb sql backup-policy show --account-name PharmaCosmosDB --resource-group PharmaRG

Summary of Changes
    Component           What We Added
    AKS Auto-Scaling	HPA to scale pods based on CPU usage (2→10)
    Cosmos DB Backups	Continuous 30-day backups + restore CLI
    CI/CD Pipeline      Added HPA deploy + backup verification

Would you like to add alerting (e.g., Slack notifications for scaling events) next?