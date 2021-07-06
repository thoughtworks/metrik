# Contributing

First off, thank you for considering contributing to MetriK. It's people like you that make it a great tool.

# Your First Contribution

You can contribute through many ways, which including but not limited to:

- Report or fix an [Issue](https://github.com/thoughtworks/metrik/issues)
    - If you've noticed a bug or have a feature request, make one! It's generally best if you get confirmation of your bug or approval for your feature request this way before starting to code

- Review or make a [Pull Request](https://github.com/thoughtworks/metrik/pulls)
    - Feel free to ask for help, everyone is a beginner at first :)
    - Working on your first Pull Request? You can learn how from [HERE](https://github.com/firstcontributions/first-contributions)
- Update the [Documentation](https://github.com/thoughtworks/metrik)
    - If the current docs are confusing for you, others may feel the same! Help us to make it better



# Getting Started
## Environment Setup
### Backend

1. `cd backend`
2. Setup MongoDB by running `./mongodb-setup/mongodb-for-local/setup-mongodb.sh`
3. Run `./gradlew clean bootRun`
4. Now you can access backend Swagger-ui by access `http://localhost:9000/swagger-ui/index.html`

### Frontend

1. `cd frontend`
2. `npm install`
3. `npm run start:local-api`
4. Now you can access it from: `http://localhost:2333/`

## Architecture and Data Flow

<div><img src="https://raw.githubusercontent.com/thoughtworks/metrik/main/.doc/img/architecture.png" height=100% width=100%></div>

# Conventions

## Commit Message

We recommend to use [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) for better readability.

The commit message should be structured as follows:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

