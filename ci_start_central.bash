full_path=$(realpath $0)
dir_path=$(dirname $full_path)
docker run --name archics -d -v "$dir_path/ServeurCentral/target":/app lwieske/java-8 java -jar /app/ServeurCentral-1.0-SNAPSHOT-jar-with-dependencies.jar