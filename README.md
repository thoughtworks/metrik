<!-- PROJECT SHIELDS -->
[![Tag](https://img.shields.io/github/v/tag/four-keys/four-key-metrics.svg?style=flat)](https://github.com/four-keys/four-key-metrics/tags)
[![Contributors](https://img.shields.io/github/contributors/four-keys/four-key-metrics.svg?style=flat)](https://github.com/four-keys/four-key-metrics/graphs/contributors)
[![Issues](https://img.shields.io/github/issues-raw/four-keys/four-key-metrics.svg?style=flat)](https://github.com/four-keys/four-key-metrics/issues)
[![Licnece](https://img.shields.io/github/license/four-keys/four-key-metrics.svg?style=flat)](https://github.com/four-keys/four-key-metrics/blob/main/LICENSE.txt)

[![CircleCI](https://circleci.com/gh/four-keys/four-key-metrics.svg?style=shield&circle-token=b34a27ea76a1d28d669eba1e36b0e2cbc6f6e5f8)](https://app.circleci.com/pipelines/github/four-keys/four-key-metrics)


<!-- PROJECT TITLE -->
<h1 align="center">
  <sub>
  <img  src="https://i.loli.net/2021/02/10/r4ZClmDFd5OEfAY.png"
        height=7%
        width=7%>
  </sub>
  Four Key Metrics
</h1>
<p align="center">
<sup>
     <i> Developed by SEA team, ThoughtWorks Inc.</i>
</sup>
<br>
</p>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ul>
    <li><a href="#about-the-project">About the Project</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
  </ul>
</details>


<!-- ABOUT THE PROJECT -->
## About the Project
Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<!-- USAGE -->
## Usage
Follow the two steps below to run the tool, and measure the four key metrics of your projects.

### Install and run

The product is released to Docker repository `public.ecr.aws/j2s5d3z8/4-key-metrics`. Please ensure that [Docker](https://www.docker.com) has already installed on your OS.

You can run the container locally via the following command:

``` bash
docker run -d -p 80:80 --name 4km public.ecr.aws/j2s5d3z8/4-key-metrics:1.1.0
```

### Configure your projects

After the container is running on your machine. Go to your favourate browser and open the app. If running in local that would be `http://localhost:80/`.

1. Start the configuration:
<div><img src="https://i.loli.net/2021/03/18/qS12tGAP6J3BO7s.png" height=60% width=60%></div>

2. And the charts will be available at the main page:
<div><img src="https://i.loli.net/2021/03/18/5UVLrShvBGpwtkI.png" height=60% width=60%></div>

3. Also the full screen view if you want to put it on big screens:
<div><img src="https://i.loli.net/2021/03/18/HntUEbz2cD4kNBu.png" height=60% width=60%></div>


### Advanced usage

If you would like to keep the 4-key-metrics data to avoid losing any data when remove container, you
can mount the database folder `/data/db` out. And logs are also availalbe if you mount the log folder `/app/logs`. As shown in the example below:

``` bash
docker run -d -p 80:80 --name 4km -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:1.1.0
```

<!-- GETTING STARTED -->
## Getting Started
Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<!-- CONTRIBUTING -->
## Contributing
Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.

<!-- LICENSE -->
## License
Distributed under the MIT License. See `LICENSE` for more information

