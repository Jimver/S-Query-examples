#!/usr/bin/env bash

JET_TEST_DIR="/mnt/c/Users/Jim/IdeaProjects/jet-test"
TAR_FILE="hazelcast-jet-4.5-SNAPSHOT"

REMOTE_HOSTS=(node1 node2)
REMOTE_USERS=(node1 node2)

HOST_NUM=${#REMOTE_HOSTS[@]}
USER_NUM=${#REMOTE_USERS[@]}

JARS=(
  "query-state-job/target/query-state-job-1.0-SNAPSHOT.jar"
  "stateful-stream-job/target/stateful-stream-job-1.0-SNAPSHOT.jar"
  "two-counter-job/target/two-counter-job-1.0-SNAPSHOT.jar"
  "sample-imaps/target/sample-imaps-1.0-SNAPSHOT.jar"
)

if [ "$HOST_NUM" -ne "$USER_NUM" ];then
  echo "Different number of hosts and users!" 1>&2
  exit 1;
fi

for ((i=0;i<HOST_NUM;i++)); do
  for JAR in "${JARS[@]}"
  do
    scp "$JET_TEST_DIR/${JAR}" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/lib/"
  done
done