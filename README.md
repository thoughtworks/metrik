#   

<a rel="license" href="https://circleci.com/gh/twlabs/SEA-4-Key-Metrics-image"><img align="right" alt="CircleCI status" style="border-width:0" src="https://circleci.com/gh/twlabs/SEA-4-Key-Metrics-image.svg?style=svg&circle-token=bef0167b698070df83f9d2a59af5930fb25a98ff" /></a>
<h1 align="center">
<sub>
<img  src="https://i.loli.net/2021/02/10/r4ZClmDFd5OEfAY.png"
      height=10%
      width=10%>
</sub>
4 Key Metrics image
</h1>
<p align="center">
<sup>
     <i> Developed by SEA MU, ThoughtWorks Inc.</i>
</sup>
<br>
</p>


Pack `4-Key-Metrics` production Docker image and publish to AWS ECR public repository with CircleCI.

Docker image repository: https://gallery.ecr.aws/j2s5d3z8/4-key-metrics

## About image

After manually trigger the CircleCI workflow, the pipeline will wrap the following environments and
services in a Docker image.

* Basic System
    * Ubuntu 18.04 (bionic)

* Environments
    * [AdoptOpenJRE 11 \(Hotspot JVM\)](https://adoptopenjdk.net)
    * [MongoDB 4.4.3](https://github.com/docker-library/mongo/blob/bc7b2d08696f84ef9b85cf98cfefb189c6a1f30e/4.4/Dockerfile)
    * [Nginx](https://www.nginx.com)

* Artifacts
    * [4-Key-Metrics-service](https://github.com/twlabs/SEA-4-Key-Metrics-service)
    * [4-Key-Metrics-dashboard](https://github.com/twlabs/SEA-4-Key-Metrics-dashboard)

The services above are managed by [Supervisord](http://supervisord.org).

## Build a Docker image

Before manually trigger the CircleCI workflow to create Docker image, the following conditions must
be met:

* You are an employee of ThoughtWorks Inc.
* You have the permission to access
  the [SEA-4-Key-Metrics-image](https://github.com/twlabs/SEA-4-Key-Metrics-image) GitHub
  repository.
* You have the permission to access
  the [CircleCI board for `SEA-4-Key-Metrics-image`](https://app.circleci.com/pipelines/github/twlabs/SEA-4-Key-Metrics-image)
  .

If you are a ThoughtWorks employee but don't have accessing permissions, please apply as the
following steps:

1. ThoughtWorks Okta > Labs on the cloud > Other Labs Applications > SEA 4 Key Metrics
2. Click `I WANT TO` button, fill in the **GitHub username** and **application reason** then submit.
3. You will receive an email about being invited to TWLabs soon.

When all conditions are met, and you would like to build a new Docker image based on the latest
codes for production, please do as the following steps:

1. Access
   the [CircleCI board for `SEA-4-Key-Metrics-image`](https://app.circleci.com/pipelines/github/twlabs/SEA-4-Key-Metrics-image)
2. Find the latest pipeline build, click the
   button <img src="https://i.loli.net/2021/02/10/GwXkCLqJKUWD3HI.png" width=18px height=18px> (the
   first button in ACTIONS column) to manually trigger the workflow.
3. The pipeline status will turn into green and show **Success** if CircleCI successfully creates
   and publishes a new 4-Key-Metrics production Docker image to AWS ECR.

![image.png](https://i.loli.net/2021/02/10/c3i5GKspjoPdaD8.png)

## Usage

Please make sure that [Docker](https://www.docker.com) has already installed on your OS.

You can pull the latest version of 4-Key-Metrics docker image by run the following command:

``` bash
docker pull public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```

You can run the 4-Key-Metrics docker container by run the following command.

⚠️ It is necessary to publish 80 port to access the dashboard. 

``` bash
docker run -d -p 80:80 --name 4km public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```

If you would like to stop the 4-Key-Metrics container, run the following command:

``` bash
docker stop 4km
```

If you would like to remove the 4-Key-Metrics container and **all data inside**, run the following
command:

``` bash
docker rm 4km
```

### Advanced usage

If you would like to keep the 4-Key-Metrics data to avoid losing any data when remove container, you
can mount the MongoDB data folder `/data/db` out:

``` bash
docker run -d -p 80:80 --name 4km -v "/path/to/local/directory:/data/db" public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```

If you find any service doesn't work as expected, you can use the same way as above to mount the log
folder `/app/logs` out to debug.

If you would like test backend API with Swagger UI (port `9000`) or connect to database with a
MongoDB client (port `27017`), you can publish those ports when run docker container.

``` bash
docker run -d -p 80:80 -p 9000:9000 -p 27017:27017 --name 4km -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```