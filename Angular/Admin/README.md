## Quick start
**Make sure you have Node version >= 8.0 and NPM >= 5**

In .npmrc add

```bash
ca=
strict-ssl=false
//192.168.1.46:8443/repository/frontendCore/:_authToken=682a2856-063d-39eb-a3de-db4ebfc2a3f6
```

Install

```bash
cd ..
npm i
cd SiteHouseHolder
npm run start
```

Open [http://localhost:4200](localhost:4200)

## Dependencies
What you need to run this app:
* `node` and `npm` (`brew install node`)
* Ensure you're running the latest versions Node `8.x.x`+ (or `v10.x.x`) and NPM `5.x.x`+

> If you have `nvm` installed, which is highly recommended (`brew install nvm`) you can do a `nvm install --lts && nvm use` in `$` to run with the latest Node LTS. You can also have this `zsh` done for you [automatically](https://github.com/creationix/nvm#calling-nvm-use-automatically-in-a-directory-with-a-nvmrc-file)

Once you have those, you should install these globals with `npm install --global`:
* `webpack` (`npm install --global webpack`)
* `webpack-dev-server` (`npm install --global webpack-dev-server`)
* `karma` (`npm install --global karma-cli`)
* `protractor` (`npm install --global protractor`)
* `typescript` (`npm install --global typescript`)

## Add package
https://dev.nvx.me/angular-cli-ref/#ng-generate

https://github.com/angular/angular-cli

Generate your package in one of this folder 
* components
* forms
* directives
* pipes
* services

```bash
# add module
ng g m components/my-component
# with routung
ng g m --routing page/my-component
# add component
ng g c components/my-component  
# add service
ng g s service/common/src/my-service
# copy README.md package.json
# ...
# add demo (developer) page
ng g c demo/myComponentDemo
# develop and testting
# ....
# publish package
npm publish
```

### build Docker
```bash
npm run build:docker
```
#### MacOS:

`brew cask install docker`

And run docker by Mac bottom menu> launchpad > docker (on first run docker will ask you about password)

#### Ubuntu:

```
sudo apt-get update
sudo apt-key adv --keyserver hkp://p80.pool.sks-keyservers.net:80 --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
sudo apt-add-repository 'deb https://apt.dockerproject.org/repo ubuntu-xenial main'
sudo apt-get update
apt-cache policy docker-engine
sudo apt-get install -y docker-engine
sudo systemctl status docker  # test:  shoud be ‘active’
```
And add your user to docker group (to avoid `sudo` before using `docker` command in future):
```
sudo usermod -aG docker $(whoami)
```
and logout and login again.

### Build image

Because *node.js* is big memory consumer you need 1-2GB RAM or virtual memory to build docker image
(it was successfully tested on machine with 512MB RAM + 2GB virtual memory - building process take 7min)

Go to main project folder. To build big (~280MB) image which has cached data and is able to **FAST** rebuild  
(this is good for testing or staging environment) type:

`docker build -t frontendCore .`

To build **SMALL** (~20MB) image without cache (so each rebuild will take the same amount of time as first build)
(this is good for production environment) type:

`docker build --squash="true" -t sitehouseholder .`

The **sitehouseholder** name used in above commands is only example image name.
To remove intermediate images created by docker on build process, type:

`docker rmi -f $(docker images -f "dangling=true" -q)`

### Run image

To run created docker image on [localhost:4300](localhost:4300) type (parameter `-p 4300:80` is host:container port mapping)

`docker run --name sitehouseholder -p 4300:80 sitehouseholder &`

And that's all, you can open browser and go to [localhost:4300](localhost:4300).

### Build and Run image using docker-compose

To create and run docker image on [localhost:4300](localhost:4300) as part of large project you may use **docker-compose**. Type 

`docker-compose up &`

And that's all, you can open browser and go to [localhost:4300](localhost:4300).


### Run image on sub-domain

If you want to run image as virtual-host on sub-domain you must setup [proxy](https://github.com/jwilder/nginx-proxy)
. You should install proxy and set sub-domain in this way:

 ```
 docker pull jwilder/nginx-proxy:alpine
 docker run -d -p 80:80 --name nginx-proxy -v /var/run/docker.sock:/tmp/docker.sock:ro jwilder/nginx-proxy:alpine
 ```

 And in your `/etc/hosts` file (linux) add line: `127.0.0.1 sitehouseholder.your-domain.com` or in yor hosting add
 folowing DNS record (wildchar `*` is handy because when you add new sub-domain in future, you don't need to touch/add any DNS record)

 ```
 Type: CNAME
 Hostname: *.your-domain.com
 Direct to: your-domain.com
 TTL(sec): 43200
 ```

And now you are ready to run image on subdomain by:

```
docker run -e VIRTUAL_HOST=sitehouseholder.your-domain.com --name sitehouseholder sitehouseholder &
```

### Login into docker container

`docker exec -t -i sitehouseholder /bin/bash`