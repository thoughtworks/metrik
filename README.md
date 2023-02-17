<!-- PROJECT SHIELDS -->
[![All Contributors](https://img.shields.io/badge/all_contributors-26-orange.svg?style=flat-square)](#contributors-)
[![Issues](https://img.shields.io/github/issues-raw/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/issues)
[![Licnece](https://img.shields.io/github/license/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt)

[![Backend test](https://github.com/thoughtworks/metrik/actions/workflows/backend_test.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/backend_test.yaml)
[![Frontend test](https://github.com/thoughtworks/metrik/actions/workflows/frontend_test.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/frontend_test.yaml)
[![codecov](https://codecov.io/gh/thoughtworks/metrik/branch/main/graph/badge.svg?token=KON1ADTCCD)](https://codecov.io/gh/thoughtworks/metrik)

[![Release](https://img.shields.io/github/v/release/thoughtworks/metrik.svg?include_prereleases&style=flat)](https://github.com/thoughtworks/metrik/releases)

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

*Read this in other languages:* [English](README.md), [简体中文](README-CH.md)

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ul>
    <li><a href="#about-the-project">About the Project</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#how-to-compute-faqs">How to Compute</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#more">More</a></li>
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
- [x] GitHub Actions
- [x] Buddy
- [ ] CircleCI

  ...and more on the way

<!-- END OF ABOUT THE PROJECT -->


<!-- USAGE -->
## Usage
Follow the two steps below to run the tool, and measure the four key metrics of your projects.

### Install and run

The product is released to an ECR Docker repository `public.ecr.aws/j2s5d3z8/4-key-metrics`. Please follow the steps:
1. Ensure [Docker](https://www.docker.com) has already installed on your OS.
2. Find available [release versions](https://github.com/thoughtworks/metrik/releases) in the release page.  
   Or, you can find all history versions from our [image repository](https://gallery.ecr.aws/j2s5d3z8/4-key-metrics)
3. Run the container locally via the following command:
``` bash
docker run -d -p 80:80 --name metrik public.ecr.aws/j2s5d3z8/4-key-metrics:latest
```
*⚠️ We use port 80 to access the app. You may switch to any other port in case port 80 is occupied by other apps running on your machine.*  
*⚠️ The `latest` tag matches the most recent version of this repository. Thus using public.ecr.aws/j2s5d3z8/4-key-metrics:latest or public.ecr.aws/j2s5d3z8/4-key-metrics will ensure you are running the most up to date version of this image.*  
If you want to stick to a specific version tag, remember there no "v" in version name. e.g. public.ecr.aws/j2s5d3z8/4-key-metrics:1.1.10


### Configure your projects

After the container is running on your machine. Go to your favourite browser and open the app. If running in local that would be `http://localhost:80/`.

1. Start the configuration:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step1.png" height=70% width=70%></div>

2. And the charts for each key metric will be available at the main page:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step2.png" height=70% width=70%></div>

3. Also the full screen view if you want to put it on big screens:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step3.png" height=70% width=70%></div>

### Advanced usage

If you would like to keep the 4-key-metrics data to avoid losing any data when remove container, you
can mount the database folder `/data/db` out. And logs are also available if you mount the log folder `/app/logs`. As shown in the example below:

``` bash
docker run -d -p 80:80 --name metrik -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```
<!-- END OF USAGE -->


<!-- Our Wiki -->
## How to Compute, FAQs
[See our Wiki page](https://github.com/thoughtworks/metrik/wiki) 
<!-- END OF HOW TO COMPUTE -->


<!-- CONTRIBUTING -->
## Contributing
Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

Please check our contributing guideline form [HERE](CONTRIBUTING.md)
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


<!-- MORE -->
## More
You might also like:
* [Buildvis](https://github.com/cburgmer/buildviz), transparency for your build pipeline's results and runtime
* [HeartBeat](https://github.com/thoughtworks/HeartBeat), calculates delivery metrics from CI/CD build data, revision control and project planning tools.
* [GoCD Analytics Plugin](https://extensions-docs.gocd.org/analytics/current/), provides insights into your GoCD instance.
<!-- END OF MORE -->


<!-- LICENSE -->
## License
Distributed under the MIT License. See [LICENSE](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt) for more information
<!-- END OF LICENSE -->


<!-- CONTRIBUTORS -->
## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/zhe-zhao"><img src="https://avatars.githubusercontent.com/u/7913366?v=4?s=100" width="100px;" alt="Zhe ZHAO"/><br /><sub><b>Zhe ZHAO</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=zhe-zhao" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/hyrepo"><img src="https://avatars.githubusercontent.com/u/15993536?v=4?s=100" width="100px;" alt="Hao Yang"/><br /><sub><b>Hao Yang</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=hyrepo" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://www.hackl0us.com"><img src="https://avatars.githubusercontent.com/u/10215166?v=4?s=100" width="100px;" alt="Jason Zhang"/><br /><sub><b>Jason Zhang</b></sub></a><br /><a href="#infra-Hackl0us" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://reeli.github.io/"><img src="https://avatars.githubusercontent.com/u/8966348?v=4?s=100" width="100px;" alt="橘子小睿"/><br /><sub><b>橘子小睿</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=reeli" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/binfang-huang"><img src="https://avatars.githubusercontent.com/u/51937651?v=4?s=100" width="100px;" alt="HUANG Binfang"/><br /><sub><b>HUANG Binfang</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=binfang-huang" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/HeZhuConnie"><img src="https://avatars.githubusercontent.com/u/32558396?v=4?s=100" width="100px;" alt="HeZhuConnie"/><br /><sub><b>HeZhuConnie</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=HeZhuConnie" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/mengqiuPeng"><img src="https://avatars.githubusercontent.com/u/81615273?v=4?s=100" width="100px;" alt="Mengqiu PENG"/><br /><sub><b>Mengqiu PENG</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=mengqiuPeng" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kikychn"><img src="https://avatars.githubusercontent.com/u/17060422?v=4?s=100" width="100px;" alt="Kiky"/><br /><sub><b>Kiky</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=kikychn" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/RongZhou1"><img src="https://avatars.githubusercontent.com/u/31197480?v=4?s=100" width="100px;" alt="Rong"/><br /><sub><b>Rong</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=RongZhou1" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Piaopiao-TW"><img src="https://avatars.githubusercontent.com/u/77046205?v=4?s=100" width="100px;" alt="Piaopiao-TW"/><br /><sub><b>Piaopiao-TW</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=Piaopiao-TW" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/zydxt"><img src="https://avatars.githubusercontent.com/u/9389762?v=4?s=100" width="100px;" alt="zydxt"/><br /><sub><b>zydxt</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=zydxt" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ifeelcold1824"><img src="https://avatars.githubusercontent.com/u/63954164?v=4?s=100" width="100px;" alt="ifeelcold1824"/><br /><sub><b>ifeelcold1824</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ifeelcold1824" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/klxq"><img src="https://avatars.githubusercontent.com/u/9253941?v=4?s=100" width="100px;" alt="快乐心情"/><br /><sub><b>快乐心情</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=klxq" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gtycherry"><img src="https://avatars.githubusercontent.com/u/8056464?v=4?s=100" width="100px;" alt="Taiyu Guo"/><br /><sub><b>Taiyu Guo</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=gtycherry" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/sasasakuna"><img src="https://avatars.githubusercontent.com/u/5813846?v=4?s=100" width="100px;" alt="sasasakuna"/><br /><sub><b>sasasakuna</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=sasasakuna" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Miyakee"><img src="https://avatars.githubusercontent.com/u/11499267?v=4?s=100" width="100px;" alt="Chen"/><br /><sub><b>Chen</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=Miyakee" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ivy-pugai"><img src="https://avatars.githubusercontent.com/u/75413818?v=4?s=100" width="100px;" alt="ivy-pugai"/><br /><sub><b>ivy-pugai</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ivy-pugai" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ivy-zxx"><img src="https://avatars.githubusercontent.com/u/57651346?v=4?s=100" width="100px;" alt="ZengXiaoXing"/><br /><sub><b>ZengXiaoXing</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ivy-zxx" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/HeyWen"><img src="https://avatars.githubusercontent.com/u/43331064?v=4?s=100" width="100px;" alt="HeyWen"/><br /><sub><b>HeyWen</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=HeyWen" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/yong-wang1"><img src="https://avatars.githubusercontent.com/u/59590942?v=4?s=100" width="100px;" alt="yong-wang1"/><br /><sub><b>yong-wang1</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=yong-wang1" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/paulaasf"><img src="https://avatars.githubusercontent.com/u/31252141?v=4?s=100" width="100px;" alt="Paula Ferreira"/><br /><sub><b>Paula Ferreira</b></sub></a><br /><a href="#infra-paulaasf" title="Infrastructure (Hosting, Build-Tools, etc)">🚇</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/twpei"><img src="https://avatars.githubusercontent.com/u/80678709?v=4?s=100" width="100px;" alt="twpei"/><br /><sub><b>twpei</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=twpei" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/hstruebe"><img src="https://avatars.githubusercontent.com/u/5832390?v=4?s=100" width="100px;" alt="Henning S."/><br /><sub><b>Henning S.</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=hstruebe" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://about.me/jainsahab"><img src="https://avatars.githubusercontent.com/u/5915092?v=4?s=100" width="100px;" alt="Prateek"/><br /><sub><b>Prateek</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=jainsahab" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://karuppiah7890.github.io/blog/"><img src="https://avatars.githubusercontent.com/u/12808424?v=4?s=100" width="100px;" alt="Karuppiah Natarajan"/><br /><sub><b>Karuppiah Natarajan</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=karuppiah7890" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="http://www.fabioformosa.it"><img src="https://avatars.githubusercontent.com/u/4976513?v=4?s=100" width="100px;" alt="Fabio Formosa"/><br /><sub><b>Fabio Formosa</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=fabioformosa" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/kevinlzw"><img src="https://avatars.githubusercontent.com/u/13826660?v=4?s=100" width="100px;" alt="Zhongwen Lian"/><br /><sub><b>Zhongwen Lian</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=kevinlzw" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Devonzhang"><img src="https://avatars.githubusercontent.com/u/37068153?v=4?s=100" width="100px;" alt="Devonzhang"/><br /><sub><b>Devonzhang</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=Devonzhang" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ashishsoni123"><img src="https://avatars.githubusercontent.com/u/2226564?v=4?s=100" width="100px;" alt="Ashish Soni"/><br /><sub><b>Ashish Soni</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ashishsoni123" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/marcohutzsch1234"><img src="https://avatars.githubusercontent.com/u/39520486?v=4?s=100" width="100px;" alt="Marco Hutzsch"/><br /><sub><b>Marco Hutzsch</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=marcohutzsch1234" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://bandism.net/"><img src="https://avatars.githubusercontent.com/u/22633385?v=4?s=100" width="100px;" alt="Ikko Ashimine"/><br /><sub><b>Ikko Ashimine</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=eltociear" title="Documentation">📖</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/HE00L"><img src="https://avatars.githubusercontent.com/u/35286550?v=4?s=100" width="100px;" alt="HE00L"/><br /><sub><b>HE00L</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=HE00L" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/ATPEEE"><img src="https://avatars.githubusercontent.com/u/72388996?v=4?s=100" width="100px;" alt="ATPEEE"/><br /><sub><b>ATPEEE</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ATPEEE" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/LixingSun"><img src="https://avatars.githubusercontent.com/u/19371539?v=4?s=100" width="100px;" alt="Sun Lixing"/><br /><sub><b>Sun Lixing</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=LixingSun" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/RoujingLiu"><img src="https://avatars.githubusercontent.com/u/94289697?v=4?s=100" width="100px;" alt="RoujingLiu"/><br /><sub><b>RoujingLiu</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=RoujingLiu" title="Code">💻</a></td>
    </tr>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/Ingridwyh090"><img src="https://avatars.githubusercontent.com/u/104956827?v=4?s=100" width="100px;" alt="Ingridwyh090"/><br /><sub><b>Ingridwyh090</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=Ingridwyh090" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/xloypaypa"><img src="https://avatars.githubusercontent.com/u/10886456?v=4?s=100" width="100px;" alt="xloypaypa"/><br /><sub><b>xloypaypa</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=xloypaypa" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/razubuddy"><img src="https://avatars.githubusercontent.com/u/51358366?v=4?s=100" width="100px;" alt="razu"/><br /><sub><b>razu</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=razubuddy" title="Code">💻</a></td>
      <td align="center" valign="top" width="14.28%"><a href="https://blog.wildan.us"><img src="https://avatars.githubusercontent.com/u/7030099?v=4?s=100" width="100px;" alt="Wildan S. Nahar"/><br /><sub><b>Wildan S. Nahar</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=wildan3105" title="Code">💻</a></td>
    </tr>
  </tbody>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
