#!/usr/bin/env bash

JET_TEST_DIR="/mnt/c/Users/Jim/IdeaProjects/jet-test"
TAR_FILE="hazelcast-jet-4.4.1-SNAPSHOT"

REMOTE_HOSTS=(node1 node2)
REMOTE_USERS=(node1 node2)

HOST_NUM=${#REMOTE_HOSTS[@]}
USER_NUM=${#REMOTE_USERS[@]}

JOBS=(
  "query-state-job"
  "stateful-stream-job"
  "two-counter-job"
  "sample-imaps"
)

USER_JOBS=(
  "user-jobs-shared"
  "user-order-job"
  "user-order-query-job"
  "user-tracking-job"
  "user-tracking-query-job"
)

JARS=()

for JOB in "${JOBS[@]}"; do
  JAR="$JOB/target/$JOB-1.0-SNAPSHOT.jar"
  JARS+=( "$JAR" )
done

for JOB in "${USER_JOBS[@]}"; do
  JAR="user-jobs/$JOB/target/$JOB-1.0-SNAPSHOT.jar"
  JARS+=( "$JAR" )
done

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