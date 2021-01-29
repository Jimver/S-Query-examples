#!/usr/bin/env bash

JET_SRC_DIR="/mnt/c/Users/Jim/IdeaProjects/hazelcast-jet"
TAR_FILE="hazelcast-jet-4.4-SNAPSHOT"

JET_TEST_DIR="/mnt/c/Users/Jim/IdeaProjects/jet-test"

REMOTE_HOSTS=(node1 node2)
REMOTE_USERS=(node1 node2)

HOST_NUM=${#REMOTE_HOSTS[@]}
USER_NUM=${#REMOTE_USERS[@]}

if [ "$HOST_NUM" -ne "$USER_NUM" ];then
  echo "Different number of hosts and users!" 1>&2
  exit 1;
fi

echo "$JET_SRC_DIR/test"

for ((i=0;i<HOST_NUM;i++)); do
  scp "$JET_SRC_DIR/hazelcast-jet-distribution/target/${TAR_FILE}.tar.gz" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/"
  ssh "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}" "tar xzvf ${TAR_FILE}.tar.gz"
  scp "$JET_TEST_DIR/custom-attributes/target/custom-attributes-1.0-SNAPSHOT.jar" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/lib/"
  scp "$JET_TEST_DIR/query-state-job/target/query-state-job-1.0-SNAPSHOT.jar" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/lib/"
  scp "$JET_TEST_DIR/stateful-stream-job/target/stateful-stream-job-1.0-SNAPSHOT.jar" "${REMOTE_USERS[$i]}"@"${REMOTE_HOSTS[$i]}":"/home/${REMOTE_USERS[$i]}/${TAR_FILE}/lib/"
done

