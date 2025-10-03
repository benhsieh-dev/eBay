#!/bin/bash
# Deploy eBay Marketplace to AWS Lightsail Container Service
# Requires: AWS CLI installed and configured

set -e  # Exit on any error

echo "🚀 Starting AWS Lightsail deployment for eBay Marketplace..."

# Configuration
SERVICE_NAME="ebay-marketplace"
CONTAINER_NAME="app"
POWER="micro"  # $10/month
SCALE=1
REGION="us-east-1"  # Default region

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if AWS CLI is installed
if ! command -v aws &> /dev/null; then
    echo -e "${RED}❌ AWS CLI is not installed. Please install it first:${NC}"
    echo "https://docs.aws.amazon.com/cli/latest/userguide/getting-started-install.html"
    exit 1
fi

# Check if user is logged in to AWS
if ! aws sts get-caller-identity &> /dev/null; then
    echo -e "${RED}❌ AWS CLI is not configured. Please run 'aws configure' first${NC}"
    exit 1
fi

echo -e "${GREEN}✅ AWS CLI is configured${NC}"

# Build Docker image locally
echo -e "${YELLOW}📦 Building Docker image...${NC}"
docker build -t ${SERVICE_NAME}:latest .

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Docker build failed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Docker image built successfully${NC}"

# Create or update Lightsail container service
echo -e "${YELLOW}🔧 Creating/updating Lightsail container service...${NC}"

# Check if service already exists
if aws lightsail get-container-services --service-name ${SERVICE_NAME} &> /dev/null; then
    echo -e "${YELLOW}📝 Service exists, updating...${NC}"
    UPDATE_MODE=true
else
    echo -e "${YELLOW}🆕 Creating new service...${NC}"
    UPDATE_MODE=false
    
    # Create the container service
    aws lightsail create-container-service \
        --service-name ${SERVICE_NAME} \
        --power ${POWER} \
        --scale ${SCALE} \
        --tags key=Project,value=eBay-Marketplace key=Environment,value=Production
    
    echo -e "${GREEN}✅ Container service created${NC}"
    echo -e "${YELLOW}⏳ Waiting for service to be ready (this can take 5-10 minutes)...${NC}"
    
    # Wait for service to be ready
    while true; do
        STATE=$(aws lightsail get-container-services --service-name ${SERVICE_NAME} --query 'containerServices[0].state' --output text)
        if [ "$STATE" = "READY" ]; then
            break
        fi
        echo -e "${YELLOW}⏳ Service state: $STATE, waiting...${NC}"
        sleep 30
    done
fi

echo -e "${GREEN}✅ Container service is ready${NC}"

# Push container image to Lightsail
echo -e "${YELLOW}📤 Pushing Docker image to Lightsail...${NC}"

aws lightsail push-container-image \
    --service-name ${SERVICE_NAME} \
    --label ${CONTAINER_NAME} \
    --image ${SERVICE_NAME}:latest

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Failed to push image to Lightsail${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Image pushed successfully${NC}"

# Create deployment configuration
echo -e "${YELLOW}⚙️  Creating deployment configuration...${NC}"

# Get the latest image name from Lightsail
IMAGE_NAME=$(aws lightsail get-container-images --service-name ${SERVICE_NAME} --query 'containerImages[0].image' --output text)

# Create deployment JSON
cat > deployment-config.json << EOF
{
    "containers": {
        "${CONTAINER_NAME}": {
            "image": "${IMAGE_NAME}",
            "ports": {
                "8080": "HTTP"
            },
            "environment": {
                "SPRING_PROFILES_ACTIVE": "production",
                "SERVER_PORT": "8080",
                "SPRING_KAFKA_ENABLED": "false"
            }
        }
    },
    "publicEndpoint": {
        "containerName": "${CONTAINER_NAME}",
        "containerPort": 8080,
        "healthCheck": {
            "healthyThreshold": 2,
            "unhealthyThreshold": 5,
            "timeoutSeconds": 10,
            "intervalSeconds": 30,
            "path": "/",
            "successCodes": "200"
        }
    }
}
EOF

# Deploy the container
echo -e "${YELLOW}🚀 Deploying container...${NC}"

aws lightsail create-container-service-deployment \
    --service-name ${SERVICE_NAME} \
    --cli-input-json file://deployment-config.json

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Deployment failed${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Deployment initiated${NC}"

# Clean up temporary file
rm deployment-config.json

# Get service URL
echo -e "${YELLOW}🔍 Getting service information...${NC}"

SERVICE_URL=$(aws lightsail get-container-services --service-name ${SERVICE_NAME} --query 'containerServices[0].url' --output text)

echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
echo -e "${GREEN}📱 Your application will be available at: https://${SERVICE_URL}${NC}"
echo -e "${YELLOW}⏳ Note: It may take 5-10 minutes for the deployment to be fully active${NC}"

# Show useful commands
echo -e "\n${YELLOW}📋 Useful commands:${NC}"
echo "• Check deployment status: aws lightsail get-container-service-deployments --service-name ${SERVICE_NAME}"
echo "• View logs: aws lightsail get-container-log --service-name ${SERVICE_NAME} --container-name ${CONTAINER_NAME}"
echo "• Service details: aws lightsail get-container-services --service-name ${SERVICE_NAME}"
echo "• Delete service: aws lightsail delete-container-service --service-name ${SERVICE_NAME}"

echo -e "\n${GREEN}✨ AWS Lightsail deployment script completed!${NC}"