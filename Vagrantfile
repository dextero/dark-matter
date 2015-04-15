# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.provision "shell", path: "provision"
  config.vm.provider "virtualbox" do |v|
    # TMV compilation crashes with just 2GBs
    v.memory = 4096
    # to speed up compilation a bit
    v.cpus = 2
  end
end
