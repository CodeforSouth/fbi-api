# Florida Business Inspections (FBI) API ![](https://img.shields.io/badge/status-active-green.svg) [![Build Status](https://travis-ci.org/Code-for-Miami/fbi-api.svg?branch=master)](https://travis-ci.org/Code-for-Miami/fbi-api)

Get started through our wiki here: https://github.com/Code-for-Miami/fbi-api/wiki

## What is this?

This is a RESTful API that scrapes restaurant inspections in Miami-Dade County. The goal is to provide an easier way for developers to use local restaurant inspection data for their own personal projects.

You can check the original requirement here: https://github.com/Code-for-Miami/tasks/issues/50

## Data Source

We are using CSV data from the State of Florida: http://www.myfloridalicense.com/dbpr/sto/file_download/public-records-food-service.html

## Technical Background

This is powered by Clojure, using MYSQL database. Installation instructions here: https://github.com/Code-for-Miami/fbi-api/wiki/How-to-Install%3F

You can check the demo app here: http://138.197.90.94/

## License

The code for this repository has been released into the public domain by Code for Miami via the MIT License.

## Contributors

This project was kickstarted by [Leo Ribeiro](https://github.com/leordev) and is now maintained by [Joel Quiles](https://github.com/teh0xqb). Take a look at all [contributors here](https://github.com/Code-for-Miami/fbi-api/graphs/contributors).

## Using Docker:

Docker has been set up to easily create a development environment inside a container.
We are still not creating a production environment, although that is certainly possible.

#### Using script

Just run `start-docker.sh`

which will use the docker compose yaml file to setup all necessar services

Or, doing things manually...

#### Building an image

`sudo docker build -t=fbiapidev .`

This will download ubuntu image, if necessary, then build our custom image.

#### Start a container just for dev server
`sudo docker run -d -v .:/code -p 8080:8080 fbiapidev lein run`

#### Check running containers
`docker ps` -a to display both running and stopped containers

#### Connect to any running container to inspect services, filesystem, etc
`sudo docker exec -it <container name or id> /bin/bash`

#### Cheatsheet

run -d = detach instead of following output
run -v = link local volume to docker one
run -p = Publish a container's port(s) to the host
