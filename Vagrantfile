# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/vivid64"
  config.vm.network "private_network", ip: "192.168.33.10"

  config.vm.provision "shell", inline: <<-SHELL
    # sudo add-apt-repository ppa:openjdk-r/ppa -y
    wget https://www.rabbitmq.com/rabbitmq-signing-key-public.asc
    sudo apt-key add rabbitmq-signing-key-public.asc -y
    sudo echo "deb http://www.rabbitmq.com/debian/ testing main" >> /etc/apt/sources.list
    sudo apt-get update
    sudo apt-get install -y openjdk-8-jdk rabbitmq-server git maven

    rabbitmq-plugins enable rabbitmq_management 
    
    git clone https://github.com/xebia/microservices-breaking-up-a-monolith
  SHELL
end