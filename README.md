<!-- PROJECT SHIELDS -->
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-12-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
[![Tag](https://img.shields.io/github/v/tag/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/tags)
[![Contributors](https://img.shields.io/github/contributors/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/graphs/contributors)
[![Issues](https://img.shields.io/github/issues-raw/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/issues)
[![Licnece](https://img.shields.io/github/license/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt)

[![Frontend and Backend test](https://github.com/thoughtworks/metrik/actions/workflows/run_test_for_frontend_backend.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/run_test_for_frontend_backend.yaml)

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
    <li><a href="#how-to-compute">How to Compute</a></li>
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

The product is released to an ECR Docker repository `public.ecr.aws/j2s5d3z8/4-key-metrics`. Please follow the steps:
1. Ensure [Docker](https://www.docker.com) has already installed on your OS.
2. Find avaiable release versions in the Docker repository: [https://gallery.ecr.aws/j2s5d3z8/4-key-metrics](https://gallery.ecr.aws/j2s5d3z8/4-key-metrics)
3. Run the container locally via the following command:

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


<!-- HOW TO COMPUTE -->
## How to compute
[See our Wiki page](https://github.com/thoughtworks/metrik/wiki) 
<!-- END OF HOW TO COMPUTE -->


<!-- CONTRIBUTING -->
## Contributing
Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

Please check our contributing guideline form [HERE](https://github.com/thoughtworks/metrik/blob/main/CONTRIBUTING.md)
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

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/zhe-zhao"><img src="https://avatars.githubusercontent.com/u/7913366?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Zhe ZHAO</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=zhe-zhao" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/hyrepo"><img src="https://avatars.githubusercontent.com/u/15993536?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Hao Yang</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=hyrepo" title="Code">💻</a></td>
    <td align="center"><a href="http://www.hackl0us.com"><img src="https://avatars.githubusercontent.com/u/10215166?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jason Zhang</b></sub></a><br /><a href="#infra-Hackl0us" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
    <td align="center"><a href="https://reeli.github.io/"><img src="https://avatars.githubusercontent.com/u/8966348?v=4?s=100" width="100px;" alt=""/><br /><sub><b>橘子小睿</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=reeli" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/binfang-huang"><img src="https://avatars.githubusercontent.com/u/51937651?v=4?s=100" width="100px;" alt=""/><br /><sub><b>HUANG Binfang</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=binfang-huang" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/HeZhuConnie"><img src="https://avatars.githubusercontent.com/u/32558396?v=4?s=100" width="100px;" alt=""/><br /><sub><b>HeZhuConnie</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=HeZhuConnie" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/mengqiuPeng"><img src="https://avatars.githubusercontent.com/u/81615273?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Mengqiu PENG</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=mengqiuPeng" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/kikychn"><img src="https://avatars.githubusercontent.com/u/17060422?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Kiky</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=kikychn" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/RongZhou1"><img src="https://avatars.githubusercontent.com/u/31197480?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Rong</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=RongZhou1" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Piaopiao-TW"><img src="https://avatars.githubusercontent.com/u/77046205?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Piaopiao-TW</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=Piaopiao-TW" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/zydxt"><img src="https://avatars.githubusercontent.com/u/9389762?v=4?s=100" width="100px;" alt=""/><br /><sub><b>zydxt</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=zydxt" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ifeelcold1824"><img src="https://avatars.githubusercontent.com/u/63954164?v=4?s=100" width="100px;" alt=""/><br /><sub><b>ifeelcold1824</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ifeelcold1824" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!