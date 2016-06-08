# DOCKER
# ------
DOCKER_HOME="/Users/jan/dev/docker/vm"
alias denv="export DOCKER_HOST=tcp://192.168.168.168:2375"
alias dup="VAGRANT_CWD=${DOCKER_HOME} vagrant up"
alias dhalt="VAGRANT_CWD=${DOCKER_HOME} vagrant halt"
alias ddestroy="VAGRANT_CWD=${DOCKER_HOME} vagrant destroy"
alias dstatus="VAGRANT_CWD=${DOCKER_HOME} vagrant status"
alias dssh="VAGRANT_CWD=${DOCKER_HOME} vagrant ssh"
alias dstop-containers="docker ps -q | xargs docker stop"
alias dclean-containers="docker ps -aq | xargs docker rm"
alias dclean-images="docker images -q | xargs docker rmi"

alias d="docker"
alias drun="docker run -it --rm"
function dbash { drun "${@:2}" "$1" /bin/bash; }
