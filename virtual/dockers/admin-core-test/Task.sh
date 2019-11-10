#!/bin/bash
work_dir="${WORK_DIR}"
docker_file="${DOCKER_FILE_LOCATION}"

docker_build_dir="${work_dir}/docker-build"
docker_out_dir="${work_dir}/docker_image"
version="${PROJECT_VERSION}"

mkdir -p ${work_dir}
mkdir -p ${docker_build_dir}
mkdir -p ${docker_out_dir}

docker build --rm -q=false -t ${version} ${docker_file}
rm -rf ${docker_build_dir}


#docker save -o ${docker_out_dir}/${version}.tar ${version}





