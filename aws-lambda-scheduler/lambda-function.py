import boto3
import json
import os

def lambda_handler(event, context):
    ec2 = boto3.client('ec2')

    instance_ids_str = os.environ.get('INSTANCE_IDS', '')
    if not instance_ids_str:
        return {
            'statusCode': 400,
            'body': json.dumps('NO INSTANCE_IDS environment variable set')
        }

    instance_ids = [id.strip() for id in instance_ids_str.split(',')]
    action = os.environ.get('ACTION', event.get('action', 'stop'))

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
