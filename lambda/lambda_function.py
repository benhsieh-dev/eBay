import boto3
import json

ec2 = boto3.client('ec2', region_name='us-east-2')

# Your instance with Elastic IP
INSTANCE_ID = 'i-0571817e2da4c1bf6'

def lambda_handler(event, context):
  action = event.get('action', 'stop')  # 'stop' or 'start'

  try:
      if action == 'stop':
          response = ec2.stop_instances(InstanceIds=[INSTANCE_ID])
          message = f'Stopped instance {INSTANCE_ID}'
      elif action == 'start':
          response = ec2.start_instances(InstanceIds=[INSTANCE_ID])
          message = f'Started instance {INSTANCE_ID}'
      else:
          return {
              'statusCode': 400,
              'body': json.dumps(f'Invalid action: {action}')
          }

      print(message)
      print(f'Response: {response}')

      return {
          'statusCode': 200,
          'body': json.dumps(message)
      }

  except Exception as e:
      error_message = f'Error {action}ing instance: {str(e)}'
      print(error_message)
      return {
          'statusCode': 500,
          'body': json.dumps(error_message)
      }
