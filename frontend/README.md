# 4-key-metrics frontend SPA

This is the frontend SPA of the 4-key-metrics measurement tool.

# Requirement
Node Version >= 10

# Get Started
## Tech Stack
* React
* Typescript	
* Webpack
* Jest & Testing-Library For React

## Usage
* Run
```bash
npm start
```

* Build
```bash
npm run build:prod
```
if you want to analyze the built details, you can use:
```bash
npm run build:prod:analyze
```

* Test
```bash
npm test
```

## Caveats
* using git **pre-commit** hook to do type and code style checking, so if there's any type and code style errors. We won't let you commit your changes 
* using git **pre-push** hook to do unit test checking, so if there's any unit test errors, Or you don't meet the test coverage threshold. We won't let you push your commits

## Folder Structure

```text
.
├── Dockerfile
├── Dockerfile-CircleCI
├── Jenkinsfile
├── README.md
├── babel.config.js
├── clients
├── dist
├── jest.config.js
├── nginx.conf
├── package-lock.json
├── package.json
├── public
│   └── index.html
├── scripts
│   ├── constants.js
│   ├── webpack.common.js
│   ├── webpack.dev.js
│   └── webpack.prod.js
├── src
│   ├── App.tsx
│   ├── Routes.tsx
│   ├── config
│   │   ├── PageConfig.tsx
│   │   └── components
│   ├── dashboard
│   │   ├── PageDashboard.tsx
│   │   └── components
│   └── shared
│       ├── __types__
│       ├── assets
│       ├── clients
│       ├── components
│       ├── constants
│       ├── hooks
│       └── utils
├── test
│   ├── mocks
│   │   ├── fileMock.ts
│   │   └── styleMock.ts
│   └── setup.ts
└── tsconfig.json

```

1. `src` 目录主要用于放置核心业务代码。其中除 `shared` 目录之外，均按照业务 feature 进行划分，比如 `config` 和 `dashboard`
2. `src` 中的 `shared` 目录主要用于放置一些跨 feature 共享的代码:
	 - `component`: 公用组件
	 - `utils`: 一些可复用的工具函数、helpers 等
	 - `constants`
	 - `clients`
	 - `assets`
	 - `__types__`

