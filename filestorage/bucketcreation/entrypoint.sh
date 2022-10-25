#!/bin/sh

# First we need to wait until we can talk with the server...
until curl -f http://${MINIO_SERVER}:${MINIO_PORT}/minio/health/live
do
    echo "Waiting to connect to minio server..."
    sleep 5
done

# Then we need to create an alias to work with the minio server
/usr/bin/mc alias set minio http://${MINIO_SERVER}:${MINIO_PORT} ${MINIO_ROOT_USER} ${MINIO_ROOT_PASSWORD} --api S3v4
# And then we can make the bucket & set its policy
/usr/bin/mc mb --quiet minio/${MINIO_BUCKET_NAME}
/usr/bin/mc policy set download minio/${MINIO_BUCKET_NAME}


# If we would need to run for longer, we could run
#
# tail -f /dev/null
#
# Then we could maybe reuse the container for doing more with the
# mc management command.
#
# But in most cases we don't need to, so we exit.
exit 0
