Here's a step-by-step guide to deploy your GitHub-hosted app to Azure Kubernetes Service (AKS) using CI/CD with GitHub Actions, integrated with Cosmos DB.

ACR - Azure Container Registry

🛠️ Step 1: Prerequisites
You mentioned you already have:
✅ Azure account
✅ GitHub repository
✅ GitHub Secrets set up (like AZURE_CREDENTIALS)

Assuming your app is a Spring Boot (or Java) app with a Dockerfile.

✅ Step 2: Create Azure Resources
You can use the Azure CLI or Portal. Here's the CLI approach:

1. Login:
az login
2. Set variables:
RESOURCE_GROUP=pharmacy-rg
LOCATION=eastus
AKS_CLUSTER=pharmacy-aks
ACR_NAME=pharmacyacr
COSMOS_ACCOUNT=pharmacy-cosmos
COSMOS_DB_NAME=prescriptions
COSMOS_CONTAINER_NAME=orders
3. Create resources:
az group create --name $RESOURCE_GROUP --location $LOCATION

# Create AKS
az aks create --resource-group $RESOURCE_GROUP --name $AKS_CLUSTER \
  --node-count 2 --enable-addons monitoring --generate-ssh-keys

# Create ACR
az acr create --resource-group $RESOURCE_GROUP --name $ACR_NAME --sku Basic

# Attach ACR to AKS
az aks update -n $AKS_CLUSTER -g $RESOURCE_GROUP --attach-acr $ACR_NAME

# Create Cosmos DB for Mongo API (or Core SQL if preferred)
az cosmosdb create --name $COSMOS_ACCOUNT --resource-group $RESOURCE_GROUP --kind MongoDB

az cosmosdb mongodb database create --account-name $COSMOS_ACCOUNT \
  --resource-group $RESOURCE_GROUP --name $COSMOS_DB_NAME

az cosmosdb mongodb collection create --account-name $COSMOS_ACCOUNT \
  --resource-group $RESOURCE_GROUP --database-name $COSMOS_DB_NAME \
  --name $COSMOS_CONTAINER_NAME --throughput 400
✅ Step 3: Add GitHub Secrets
In your GitHub repo → Settings → Secrets:

Secret Name	Value
AZURE_CREDENTIALS	Output of az ad sp create-for-rbac --sdk-auth
REGISTRY_NAME	pharmacyacr.azurecr.io
RESOURCE_GROUP	pharmacy-rg
AKS_CLUSTER	pharmacy-aks

✅ Step 4: Create GitHub Actions Workflow
.github/workflows/deploy.yml

name: CI/CD to AKS with Cosmos DB

on:
  push:
    branches:
      - main

env:
  REGISTRY: ${{ secrets.REGISTRY_NAME }}
  IMAGE_NAME: pharmacy-app

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
    - name: Checkout code
      uses: actions/checkout@v3

    - name: Set up JDK
      uses: actions/setup-java@v3
      with:
        java-version: '17'

    - name: Build app with Maven
      run: mvn clean package -DskipTests

    - name: Login to Azure
      uses: azure/login@v1
      with:
        creds: ${{ secrets.AZURE_CREDENTIALS }}

    - name: Azure AKS credentials
      run: az aks get-credentials --name ${{ secrets.AKS_CLUSTER }} --resource-group ${{ secrets.RESOURCE_GROUP }}

    - name: Build Docker image
      run: |
        docker build -t $REGISTRY/$IMAGE_NAME:${{ github.sha }} .
        echo $REGISTRY_PASSWORD | docker login $REGISTRY -u $REGISTRY_USERNAME --password-stdin
        docker push $REGISTRY/$IMAGE_NAME:${{ github.sha }}

    - name: Deploy to AKS
      run: |
        kubectl apply -f k8s/deployment.yaml
        kubectl apply -f k8s/service.yaml
✅ Step 5: Kubernetes Manifests
k8s/deployment.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: pharmacy-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: pharmacy-app
  template:
    metadata:
      labels:
        app: pharmacy-app
    spec:
      containers:
      - name: pharmacy-app
        image: pharmacyacr.azurecr.io/pharmacy-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_DATA_MONGODB_URI
          value: mongodb://<cosmos-connection-uri>
Replace <cosmos-connection-uri> with the MongoDB URI from:

az cosmosdb keys list --name pharmacy-cosmos --resource-group pharmacy-rg --type connection-strings
✅ Step 6: Test & Monitor
Once the pipeline runs:

kubectl get pods
kubectl get svc
kubectl logs <pod-name>
Optional:

Add Ingress Controller (e.g., NGINX) for domain access

Add Azure Application Gateway + TLS

Enable Azure Monitor for logs/metrics

✅ Summary
Step	Description
1	Create AKS, ACR, Cosmos DB with Mongo API
2	Add GitHub Secrets
3	Push app and create GitHub Actions workflow
4	Deploy to AKS using kubectl
5	App connects to Cosmos DB via environment variable

Would you like:

A ready-to-use sample Java app with Cosmos DB?

Helm chart version of this deployment?

Monitoring via Prometheus + Grafana in AKS?