#!/bin/bash
project="${PR_NAME}"
repo="${REPO}"
version="${VERSION_TAG}"

docker tag ${version} ${repo}/${project}
docker push ${repo}/${project}
docker search ${repo}/${project}



