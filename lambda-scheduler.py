import json
import boto3
from datetime import datetime

def lambda_handler(event, context):
  # Your current EC2 instance ID
  INSTANCE_ID = 'i-021d853b40fb0faae'
  REGION = 'us-east-2'

  ec2 = boto3.client('ec2', region_name=REGION)

  # Get action from event (stop or start)
  action = event.get('action', 'stop')

  try:
      if action == 'stop':
          response = ec2.stop_instances(InstanceIds=[INSTANCE_ID])
          message = f'Stopped instance {INSTANCE_ID}'
      elif action == 'start':
          response = ec2.start_instances(InstanceIds=[INSTANCE_ID])
          message = f'Started instance {INSTANCE_ID}'

      return {
          'statusCode': 200,
          'body': json.dumps({
              'message': message,
              'timestamp': datetime.now().isoformat()
          })
      }
  except Exception as e:
      return {
          'statusCode': 500,
          'body': json.dumps({'error': str(e)})
      }