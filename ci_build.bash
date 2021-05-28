full_path=$(realpath $0)
dir_path=$(dirname $full_path)
docker run -i --rm --name archics-build -v "$dir_path":/usr/src/mymaven -w /usr/src/mymaven maven:3.3-jdk-8 mvn clean validate compile package install