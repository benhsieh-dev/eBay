import boto3
import json
import os
import time
import urllib.request
import urllib.error

def lambda_handler(event, context):
    ec2 = boto3.client('ec2')
    eb = boto3.client('elasticbeanstalk')

    # Get action from event input or environment variable
    action = event.get('ACTION', os.environ.get('ACTION', 'stop'))
    
    # Get EB environment name instead of EC2 tag
    eb_environment = event.get('EB_ENVIRONMENT', os.environ.get('EB_ENVIRONMENT', 'ebay-medium'))
    
    try:
        # Get EB environment instances (EB-aware discovery)
        print(f"Getting instances for EB environment: {eb_environment}")
        
        env_resources = eb.describe_environment_resources(
            EnvironmentName=eb_environment
        )
        
        instance_ids = []
        for instance in env_resources['EnvironmentResources']['Instances']:
            instance_ids.append(instance['Id'])
        
        if not instance_ids:
            return {
                'statusCode': 404,
                'body': json.dumps(f'No instances found in EB environment: {eb_environment}')
            }
            
        print(f"Found EB instances in '{eb_environment}': {instance_ids}")
        
    except Exception as e:
        print(f"Error finding EB instances: {str(e)}")
        return {
            'statusCode': 500,
            'body': json.dumps(f'Error finding EB instances: {str(e)}')
        }

    try:
        if action == 'stop':
            response = ec2.stop_instances(InstanceIds=instance_ids)
            message = f"Stopped EB instances: {instance_ids}"
            
        elif action == 'start':
            response = ec2.start_instances(InstanceIds=instance_ids)
            message = f"Started EB instances: {instance_ids}"
            
            # Wait and verify app health after start
            app_url = event.get('APP_URL', os.environ.get('APP_URL', 
                f'http://{eb_environment}.eba-e3jvit9g.us-east-2.elasticbeanstalk.com/'))
            
            health_check_result = verify_app_health(app_url)
            message += f" | Health check: {health_check_result}"
            
        else:
            return {
                'statusCode': 400,
                'body': json.dumps(f'Invalid action: {action}. Use "start" or "stop"')
            }

        print(f"Action: {action}, Response: {response}")

        return {
            'statusCode': 200,
            'body': json.dumps({
                'message': message,
                'action': action,
                'eb_environment': eb_environment,
                'instances': instance_ids,
                'response': str(response)
            })
        }

    except Exception as e:
        print(f"Error: {str(e)}")
        return {
            'statusCode': 500,
            'body': json.dumps(f'Error: {str(e)}')
        }

def verify_app_health(app_url, max_retries=10, timeout=30):
    """Verify application is responding after start"""
    for attempt in range(max_retries):
        try:
            print(f"Health check attempt {attempt + 1}/{max_retries}: {app_url}")
            
            # Use urllib instead of requests (no external dependencies)
            req = urllib.request.Request(f"{app_url}api/products")
            with urllib.request.urlopen(req, timeout=timeout) as response:
                if response.getcode() == 200:
                    return f"✅ HEALTHY (attempt {attempt + 1})"
                    
        except Exception as e:
            print(f"Health check failed: {e}")
            
        if attempt < max_retries - 1:
            time.sleep(30)  # Wait 30s between attempts
    
    return f"❌ UNHEALTHY after {max_retries} attempts"
