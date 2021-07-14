[![Publish release Docker image](https://github.com/thoughtworks/metrik/actions/workflows/build_release_docker_image.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/build_release_docker_image.yaml)
[![Tag](https://img.shields.io/github/v/tag/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/tags)

<!-- PROJECT TITLE -->
<h1 align="center">
  <sub>
  <img  src="https://raw.githubusercontent.com/thoughtworks/metrik/main/frontend/src/assets/source/logo.svg"
        height=20%
        width=20%>
  </sub>
</h1>
<p align="center">
<sup>
     <i> Maintained by SEA team, ThoughtWorks Inc.</i>
</sup>
<br>
</p>


Pack `Metrik` production Docker image and publish to AWS ECR public repository with Github Actions.

Docker image repository: https://gallery.ecr.aws/j2s5d3z8/4-key-metrics

## About image build & publish process

We use [Github Actions](https://docs.github.com/en/actions) as the project CI/CD tool. As can be seen from the Github
Actions config file under project home `$PROJECT_HOME/.github/actions` and `$PROJECT_HOME/.github/workflows`, the CI/CD
process comprises two workflows, both of which wraps the following services and tools in a Docker image:

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

The three workflows `frontend_test`, `backend_test` and `build_release_docker_image` serve the test build and release
build respectively. Each code commit/merge in `main` branch fires a test build in Github Actions.

A release build will only be triggered with an authorised release. Team can view, test and verify the image validity.
Every release is tagged with a three-part release version like `1.2.1`. The release Docker image which in turn will be
tagged by the provided git version tag `1.2.1` and made publicly accessible in the image repository.

## Usage

Please make sure that [Docker](https://www.docker.com) has already installed on your OS.

You can pull the latest version of *Metrik* docker image by run the following command:

``` bash
docker pull public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```

You can run the *Metrik* docker container by via the following command:

``` bash
docker run -d -p 80:80 --name metrik public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```

⚠️ *We use port 80 to access the frontend resources. You may switch to any other port in case port 80 is occupied by
other apps running on your machine.*

If you would like to stop the *Metrik* container, run the following command:

``` bash
docker stop metrik
```

If you would like to remove the *Metrik* container and **all data inside**, run the following command:

``` bash
docker rm metrik
```

### Advanced usage

If you would like to retain your database to avoid losing any data after removing the container, you can mount the
MongoDB data folder `/data/db` out:

``` bash
docker run -d -p 80:80 --name metrik -v "/path/to/local/directory:/data/db" public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```

If you find any service doesn't work as expected, you can use the same way as above to mount the log folder `/app/logs`
out to debug.

If you would like to view the Swagger doc of the backend API (port `9000`), or, connect to database with a MongoDB
client (port `27017`), you can publish those ports when run docker container.

``` bash
docker run -d -p 80:80 -p 9000:9000 -p 27017:27017 --name metrik -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```
