#!/usr/bin/env bash

export AWS_PAGER=""

aws ec2 terminate-instances --instance-ids $(aws ec2 describe-instances --query 'Reservations[].Instances[].InstanceId' --filters "Name=tag:type,Values=cluster" "Name=instance-state-name,Values=running" --output text)

while true; do
    aws ec2 run-instances --image-id $(aws ec2 describe-images --query 'Images[].ImageId' --owner 181094469388 --filters "Name=tag:type,Values=amazon" --output text)\
    --count 8 --instance-type c5.4xlarge --placement "GroupName=hz-cluster" --iam-instance-profile Name="hazelcast-profile" --key-name HZKeyPair \
    --security-group-ids sg-0a89167e603fc3e1e --subnet-id subnet-a09fd1ca \
    --tag-specifications 'ResourceType=instance,Tags=[{Key=type,Value=cluster}]' 'ResourceType=volume,Tags=[{Key=type,Value=cluster}]'
    if [ $? -eq 0 ]; then
        break
    fi
    sleep 5
done

sleep 10

 ./create-ssh-config.sh