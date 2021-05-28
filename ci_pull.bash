full_path=$(realpath $0)
dir_path=$(dirname $full_path)
git -C $dir_path checkout .
git -C $dir_path pull