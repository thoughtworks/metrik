<!-- PROJECT SHIELDS -->
[![All Contributors](https://img.shields.io/badge/all_contributors-26-orange.svg?style=flat-square)](#contributors-)
[![Issues](https://img.shields.io/github/issues-raw/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/issues)
[![Licnece](https://img.shields.io/github/license/thoughtworks/metrik.svg?style=flat)](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt)

[![Backend test](https://github.com/thoughtworks/metrik/actions/workflows/backend_test.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/backend_test.yaml)
[![Frontend test](https://github.com/thoughtworks/metrik/actions/workflows/frontend_test.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/frontend_test.yaml)
[![Publish release Docker image](https://github.com/thoughtworks/metrik/actions/workflows/build_release_docker_image.yaml/badge.svg)](https://github.com/thoughtworks/metrik/actions/workflows/build_release_docker_image.yaml)

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)
[![Release](https://img.shields.io/github/v/release/thoughtworks/metrik.svg?include_prereleases&style=flat)](https://github.com/thoughtworks/metrik/releases)

*多语言支持：* [English](README.md), [简体中文](README-CH.md)  
*GitHub 仓库：* [https://github.com/thoughtworks/metrik](https://github.com/thoughtworks/metrik)  

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
     <i> 由ThoughtWorks Inc SEA团队维护</i>
</sup>
<br>
</p>


<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>目录</summary>
  <ul>
    <li><a href="#关于本项目">关于本项目</a></li>
    <li><a href="#用法">使用方法</a></li>
    <li><a href="#算法">算法</a></li>
    <li><a href="#贡献">参与贡献</a></li>
    <li><a href="#快速开始">快速开始</a></li>
    <li><a href="#更多">更多</a></li>
    <li><a href="#license">License</a></li>
  </ul>
</details>
<!-- END OF PROJECT TITLE -->

<!-- ABOUT THE PROJECT -->
## 关于本项目
对于想要衡量其软件交付和运营（SDO）效能的开发团队来说，本项目是一个帮助他们从CD管道收集数据并以友好的可视化方式展示关键指标的工具。

**关键差异化因素:**
* 单页配置，简单易用.
* 具有跨多个CD平台工作的能力.
* 用户可以自行选择要分析的环境（是的，生产环境不是唯一重要的环境）。

[不知道什么是四个关键指标?](https://www.thoughtworks.com/radar/techniques/four-key-metrics)


### 集成路线
产品现在支持/计划支持的CD工具清单
- [x] Jenkins
- [x] Bamboo
- [ ] Github Actions
- [ ] CircleCI

  ...以及更多即将集成的产品

<!-- END OF ABOUT THE PROJECT -->


<!-- USAGE -->
## 用法
按照以下两个步骤来运行该工具，并测量你的项目的四个关键指标。

### 安装和运行

该产品被发布到ECR Docker仓库`public.ecr.aws/j2s5d3z8/4-key-metrics`。请按照以下步骤操作。
1. 确保[Docker](https://www.docker.com)已经安装在你的操作系统上。
2. 在发布页中查找可用的[已发布版本](https://github.com/thoughtworks/metrik/releases)。
   或者，你可以从我们的[镜像库]中找到所有的历史版本(https://gallery.ecr.aws/j2s5d3z8/4-key-metrics)
3. 通过以下命令在本地运行该容器：
``` bash
docker run -d -p 80:80 --name metrik public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```
*⚠️ 我们使用80端口来访问该应用程序。如果80端口被你机器上运行的其他应用程序占用，你可以切换到任何其他端口。  
*⚠️ `latest`标签匹配该仓库的最新版本。因此，使用 public.ecr.aws/j2s5d3z8/4-key-metrics:latest 或 public.ecr.aws/j2s5d3z8/4-key-metrics 将确保你运行的是这个镜像的最新版本*。
如果你想使用一个特定的版本标签，请记住版本名称中没有 "v"。例如，public.ecr.aws/j2s5d3z8/4-key-metrics:1.1.10


### 配置

容器在你的机器上运行后。进入你最喜欢的浏览器并打开该应用程序。如果在本地运行，那就是`http://localhost:80/`。

1. 开始配置:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step1.png" height=70% width=70%></div>

2. 随后每个关键指标的图表将出现在主页面上:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step2.png" height=70% width=70%></div>

3. 如果你想把它放在大屏幕上，还可以全屏观看:
  <div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/step3.png" height=70% width=70%></div>

### 高级用法

如果你想保留4-key-metrics的数据，以避免在删除容器时丢失任何数据，你可以将数据库文件夹`/data/db`挂出。
可以把数据库文件夹`/data/db`挂载出来。如果你挂载日志文件夹`/app/logs`，那么日志也会被保存。如下面的例子所示。

``` bash
docker run -d -p 80:80 --name metrik -v "/path/to/local/directory:/data/db" -v "/path/to/another/directory:/app/logs" public.ecr.aws/j2s5d3z8/4-key-metrics:${release_version}
```
<!-- END OF USAGE -->


<!-- HOW TO COMPUTE -->
## 算法
[详细算法请参考Wiki页面](https://github.com/thoughtworks/metrik/wiki) 
<!-- END OF HOW TO COMPUTE -->


<!-- CONTRIBUTING -->
## 贡献
贡献是使开源社区成为一个学习、激励和创造的神奇场所的原因。我们真诚的感谢你所做的任何类型的贡献.

请在[这里](https://github.com/thoughtworks/metrik/blob/main/CONTRIBUTING.md)查看我们的贡献者指南。
<!-- END OF CONTRIBUTING -->


<!-- GETTING STARTED -->
## 快速开始
该代码库由三个主要部分组成："前端"、"后端 "和 "CI"。
* 前端应用由以下技术栈构建：
  * TypeScript
  * ReactJS
  * ReCharts

  可以在[前端文件夹](https://github.com/thoughtworks/metrik/tree/main/frontend)找到更多细节。

* 后端程序由以下技术栈构建:
  * Kotlin
  * Spring Boot Web
  * MongoDB

  可以在[后端文件夹](https://github.com/thoughtworks/metrik/tree/main/backend)找到更多细节。
  
* 构建/打包的脚本在[CI文件夹](https://github.com/thoughtworks/metrik/tree/main/ci)下。
<!-- END OF GETTING STARTED -->


<!-- MORE -->
## 更多
你可能会感兴趣的类似项目:
* [Buildvis](https://github.com/cburgmer/buildviz), transparency for your build pipeline's results and runtime
* [HeartBeat](https://github.com/thoughtworks/HeartBeat), calculates delivery metrics from CI/CD build data, revision control and project planning tools.
* [GoCD Analytics Plugin](https://extensions-docs.gocd.org/analytics/current/), provides insights into your GoCD instance.
<!-- END OF MORE -->


<!-- LICENSE -->
## License
在MIT许可下发布。更多信息见[LICENSE](https://github.com/thoughtworks/metrik/blob/main/LICENSE.txt)
<!-- END OF LICENSE -->


<!-- CONTRIBUTORS -->
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
    <td align="center"><a href="https://github.com/klxq"><img src="https://avatars.githubusercontent.com/u/9253941?v=4?s=100" width="100px;" alt=""/><br /><sub><b>快乐心情</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=klxq" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/gtycherry"><img src="https://avatars.githubusercontent.com/u/8056464?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Taiyu Guo</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=gtycherry" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/sasasakuna"><img src="https://avatars.githubusercontent.com/u/5813846?v=4?s=100" width="100px;" alt=""/><br /><sub><b>sasasakuna</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=sasasakuna" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/Miyakee"><img src="https://avatars.githubusercontent.com/u/11499267?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Chen</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=Miyakee" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ivy-pugai"><img src="https://avatars.githubusercontent.com/u/75413818?v=4?s=100" width="100px;" alt=""/><br /><sub><b>ivy-pugai</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ivy-pugai" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/ivy-zxx"><img src="https://avatars.githubusercontent.com/u/57651346?v=4?s=100" width="100px;" alt=""/><br /><sub><b>ZengXiaoXing</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=ivy-zxx" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/HeyWen"><img src="https://avatars.githubusercontent.com/u/43331064?v=4?s=100" width="100px;" alt=""/><br /><sub><b>HeyWen</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=HeyWen" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/yong-wang1"><img src="https://avatars.githubusercontent.com/u/59590942?v=4?s=100" width="100px;" alt=""/><br /><sub><b>yong-wang1</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=yong-wang1" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/twpei"><img src="https://avatars.githubusercontent.com/u/80678709?v=4?s=100" width="100px;" alt=""/><br /><sub><b>twpei</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=twpei" title="Code">💻</a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/hstruebe"><img src="https://avatars.githubusercontent.com/u/5832390?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Henning S.</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=hstruebe" title="Code">💻</a></td>
    <td align="center"><a href="https://about.me/jainsahab"><img src="https://avatars.githubusercontent.com/u/5915092?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Prateek</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=jainsahab" title="Code">💻</a></td>
    <td align="center"><a href="https://karuppiah7890.github.io/blog/"><img src="https://avatars.githubusercontent.com/u/12808424?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Karuppiah Natarajan</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=karuppiah7890" title="Code">💻</a></td>
    <td align="center"><a href="http://www.fabioformosa.it"><img src="https://avatars.githubusercontent.com/u/4976513?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Fabio Formosa</b></sub></a><br /><a href="https://github.com/thoughtworks/metrik/commits?author=fabioformosa" title="Documentation">📖</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
