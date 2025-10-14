import boto3
import json
import os

def lambda_handler(event, context):
    ec2 = boto3.client('ec2')

    # Get action from event input or environment variable
    action = event.get('ACTION', os.environ.get('ACTION', 'stop'))
    
    # Get instances by tag name instead of hardcoded IDs
    tag_name = event.get('TAG_NAME', os.environ.get('TAG_NAME', 'ebay-medium'))
    
    try:
        # Find instances by Name tag
        response = ec2.describe_instances(
            Filters=[
                {'Name': 'tag:Name', 'Values': [tag_name]},
                {'Name': 'instance-state-name', 'Values': ['running', 'stopped']}
            ]
        )
        
        instance_ids = []
        for reservation in response['Reservations']:
            for instance in reservation['Instances']:
                instance_ids.append(instance['InstanceId'])
        
        if not instance_ids:
            return {
                'statusCode': 404,
                'body': json.dumps(f'No instances found with tag Name={tag_name}')
            }
            
        print(f"Found instances with tag '{tag_name}': {instance_ids}")
        
    except Exception as e:
        print(f"Error finding instances: {str(e)}")
        return {
            'statusCode': 500,
            'body': json.dumps(f'Error finding instances: {str(e)}')
        }

    try:
        if action == 'stop':
            response = ec2.stop_instances(InstanceIds=instance_ids)
            message = f"stopped instances: {instance_ids}"
        elif action == 'start':
            response = ec2.start_instances(InstanceIds=instance_ids)
            message = f"Started instances: {instance_ids}"
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
