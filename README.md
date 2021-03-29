<!-- PROJECT SHIELDS -->
[![Tag](https://img.shields.io/github/v/tag/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/tags)
[![Contributors](https://img.shields.io/github/contributors/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/graphs/contributors)
[![Issues](https://img.shields.io/github/issues-raw/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/issues)
[![Licnece](https://img.shields.io/github/license/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt)

[![CircleCI](https://circleci.com/gh/thoughtworks/metrik.svg?style=shield&circle-token=b34a27ea76a1d28d669eba1e36b0e2cbc6f6e5f8)](https://app.circleci.com/pipelines/github/thoughtworks/metrik)


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


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ul>
    <li><a href="#about-the-project">About the Project</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#license">License</a></li>
  </ul>
</details>
<!-- END OF PROJECT TITLE -->

<!-- ABOUT THE PROJECT -->
## About the Project
For development teams who wants to measure their software delivery and operational (SDO) performance, this is a tool that helps them collect data from CD pipelines and visualize the key metrics in a friendly format.

**The key differentiators:**
* One page configuration, quick and easy.
* The ability to work across different CD platforms.
* User can select the environment in which the analysis runs via a environment filter (Yes, production env is not the only one that matters)

[Don't know what are those four key metrics?](https://www.thoughtworks.com/radar/techniques/four-key-metrics)


### Integration roadmap
List of CD tools the product supports now/plan to support
- [x] Jenkins
- [x] Bamboo
- [ ] Github Actions
- [ ] CircleCI

  ...and more on the way

<!-- END OF ABOUT THE PROJECT -->


<!-- USAGE -->
## Usage
Follow the two steps below to run the tool, and measure the four key metrics of your projects.

### Install and run

The product is released to Docker repository `public.ecr.aws/j2s5d3z8/4-key-metrics`. Please ensure that [Docker](https://www.docker.com) has already installed on your OS.

You can run the container locally via the following command:

``` bash
docker run -d -p 80:80 --name metrik public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```
⚠️ We use port 80 to access the app. You may switch to any other port in case port 80 is occupied by other apps running on your machine.

### Configure your projects

After the container is running on your machine. Go to your favourate browser and open the app. If running in local that would be `http://localhost:80/`.

1. Start the configuration:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step1.png" height=70% width=70%></div>

2. And the charts for each key metric will be available at the main page:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step2.png" height=70% width=70%></div>

3. Also the full screen view if you want to put it on big screens:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step3.png" height=70% width=70%></div>

### Advanced usage

If you would like to keep the 4-key-metrics data to avoid losing any data when remove container, you
can mount the database folder `/data/db` out. And logs are also availalbe if you mount the log folder `/app/logs`. As shown in the example below:

``` bash
docker run -d -p 80:80 --name metrik -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```
<!-- END OF USAGE -->


<!-- CONTRIBUTING -->
## Contributing
Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
<!-- END OF CONTRIBUTING -->


<!-- GETTING STARTED -->
## Getting Started
The codebase comprises of three major components `frontend`, `backend`, `ci`.
* Frontend app is a web application built with:
  * TypeScript
  * ReactJS
  * ReCharts

  Go to [frontend folder](https://github.com/thoughtworks/metrik/tree/main/frontend) to find more details.

* Backend app is built with:
  * Kotlin
  * Spring Boot Web
  * MongoDB

  Go to [backend folder](https://github.com/thoughtworks/metrik/tree/main/backend) to find more details.
  
* Build/Package scripts lives in [ci folder](https://github.com/thoughtworks/metrik/tree/main/ci)
<!-- END OF GETTING STARTED -->


<!-- LICENSE -->
## License
Distributed under the MIT License. See [LICENSE](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt) for more information
<!-- END OF LICENSE -->
