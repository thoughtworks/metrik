[![CircleCI](https://circleci.com/gh/four-keys/four-key-metrics.svg?style=shield&circle-token=b34a27ea76a1d28d669eba1e36b0e2cbc6f6e5f8)](https://app.circleci.com/pipelines/github/four-keys/four-key-metrics)


<!-- PROJECT TITLE -->
<h1 align="center">
  <sub>
  <img  src="https://i.loli.net/2021/02/10/r4ZClmDFd5OEfAY.png"
        height=5%
        width=5%>
  </sub>
  Four Key Metrics
</h1>
<p align="center">
<sup>
     <i> Maintained by SEA team, ThoughtWorks Inc.</i>
</sup>
<br>
</p>


Pack `4-key-metrics` production Docker image and publish to AWS ECR public repository with CircleCI.

Docker image repository: https://gallery.ecr.aws/j2s5d3z8/4-key-metrics

## About image build & publish process

We use [CircleCI](https://circleci.com/) as the project CI/CD tool. As can be seen from the CircleCI config file under project home `$PROJECT_HOME/.circleci/config.yml`, 
the CI/CD process is comprised of two workflows, both of which wraps the following services
and tools in a Docker image:

* Basic OS
    * Ubuntu 18.04 (bionic)

* Environments
    * [AdoptOpenJRE 11 \(Hotspot JVM\)](https://adoptopenjdk.net)
    * [MongoDB 4.4.3](https://github.com/docker-library/mongo/blob/bc7b2d08696f84ef9b85cf98cfefb189c6a1f30e/4.4/Dockerfile)
    * [Nginx](https://www.nginx.com)

* Artifacts
    * Frontend SPA resources
    * Backend API jar

* Process management
  * [Supervisord](http://supervisord.org)

The two workflows `build-image-and-deploy-sandbox` `build-release-image` serve the test build and release build respectively. Each code commit/merge in `main`
branch fires a test build in CircleCI, and produces a Docker image with `latest` tag. Team can view, test and verify the image validity. 

Unlike test build, the release build can only be triggered by a three-part release version tag like `1.2.1`. The release Docker image
which in turn will be tagged by the provided git version tag `1.2.1` and made publicly accessible in the image repository.
 


## Usage

Please make sure that [Docker](https://www.docker.com) has already installed on your OS.

You can pull the latest version of 4-key-metrics docker image by run the following command:

``` bash
docker pull public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```

You can run the 4-key-metrics docker container by via the following command:

``` bash
docker run -d -p 80:80 --name 4km public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```
⚠️ *We use port 80 to access the frontend resources.
You may switch to any other port in case port 80 is occupied by other apps running on your machine.*

If you would like to stop the 4-key-metrics container, run the following command:

``` bash
docker stop 4km
```

If you would like to remove the 4-key-metrics container and **all data inside**, run the following
command:

``` bash
docker rm 4km
```

### Advanced usage

If you would like to keep the 4-key-metrics data to avoid losing any data when remove container, you
can mount the MongoDB data folder `/data/db` out:

``` bash
docker run -d -p 80:80 --name 4km -v "/path/to/local/directory:/data/db" public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```

If you find any service doesn't work as expected, you can use the same way as above to mount the log
folder `/app/logs` out to debug.

If you would like to view the Swagger doc of the backend API (port `9000`), or, connect to database with a
MongoDB client (port `27017`), you can publish those ports when run docker container.

``` bash
docker run -d -p 80:80 -p 9000:9000 -p 27017:27017 --name 4km -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```
