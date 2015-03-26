# -*- mode: ruby -*-
# vi: set ft=ruby :

$script = <<SCRIPT
#!/bin/bash

set -e

NUM_PROCESSORS=$(cat /proc/cpuinfo | grep processor | wc -l)

apt-get install -y build-essential g++ git libboost-dev libcfitsio3-dev libfftw3-dev libgsl0-dev libsqlite3-dev scons subversion

mkdir -p /opt/stochastyczne
cd /opt/stochastyczne

svn checkout http://tmv-cpp.googlecode.com/svn/tags/v0.72/ tmv-cpp-read-only
pushd tmv-cpp-read-only
    scons -j$NUM_PROCESSORS
    scons install PREFIX=/usr/local
popd

git clone http://github.com/pmelchior/shapelens
pushd shapelens
    make SPECIAL_FLAGS=-fPIC -j$NUM_PROCESSORS
    make PREFIX=/usr/local install
popd

git clone http://github.com/pmelchior/skylens
pushd skylens
    make SPECIAL_FLAGS=-fPIC -j$NUM_PROCESSORS
    make PREFIX=/usr/local install
popd

SCRIPT

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  # if not found: vagrant box add ubuntu/trusty64 https://github.com/kraksoft/vagrant-box-ubuntu/releases/download/14.04/ubuntu-14.04-amd64.box
  config.vm.box = "ubuntu/trusty64"
  config.vm.provision "shell", inline: $script
end
