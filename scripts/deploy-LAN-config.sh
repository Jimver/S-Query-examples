#!/usr/bin/env bash

TAR_FILE="hazelcast-jet-4.4.10-SNAPSHOT"

REMOTE_HOSTS=(node1 node2 node3)
REMOTE_USERS=(node1 node2 node3)

HOST_NUM=${#REMOTE_HOSTS[@]}
USER_NUM=${#REMOTE_USERS[@]}

if [ "$HOST_NUM" -ne "$USER_NUM" ];then
  echo "Different number of hosts and users!" 1>&2
  exit 1;
fi

# Copy configs
for ((i=0;i<HOST_NUM;i++)); do
  scp "hazelcast.yaml" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/config/"
  scp "hazelcast-jet.yaml" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/config/"
  scp "nexmark-jet.properties" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/"
done
