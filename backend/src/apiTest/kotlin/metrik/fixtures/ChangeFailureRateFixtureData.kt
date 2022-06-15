package metrik.fixtures

import metrik.project.domain.model.Commit
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status

val cfrPipeline = PipelineConfiguration(
    id = "6012505c42fbb8439fc08b21",
    projectId = "601cbae825c1392117aa0429",
    name = "cfr",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-cfr//4km-df/",
    type = PipelineType.JENKINS
)

val cfrExecution1 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 1,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1609462800000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609462800100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1609462830200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1609462835300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1609408800000,
            date = "2020-12-31 00:00:00 +0800"
        )
    )
)
val cfrExecution2 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 2,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1609549200000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609549200100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1609549203100,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1609549208200,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112401",
            timestamp = 1609545600000,
            date = "2021-01-2 00:00:00 +0800"
        )
    )
)
val cfrExecution3 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 3,
    duration = 50000,
    result = Status.OTHER,
    timestamp = 1609808400000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609808400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1609808430100,
            durationMillis = 10000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.OTHER,
            startTimeMillis = 1609808440200,
            durationMillis = 10000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112402",
            timestamp = 1609804800000,
            date = "2021-01-5 00:00:00 +0800"
        )
    )
)
val cfrExecution4 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 4,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1609894800000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609894800100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1609894830200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1609894835300,
            durationMillis = 5000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112403",
            timestamp = 1609891200000,
            date = "2021-01-6 00:00:00 +0800"
        )
    )
)
val cfrExecution5 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 5,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1609981200000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609981200100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1609981486684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.FAILED,
            startTimeMillis = 1609981509078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112404",
            timestamp = 1609977600000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val cfrExecution6 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 6,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1610326800000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1610326800100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.FAILED,
            startTimeMillis = 1610327086684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1610327109078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112348",
            timestamp = 1610323200000,
            date = "2021-01-11 00:00:00 +0800"
        )
    )
)
val cfrExecution7 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 7,
    duration = 621278,
    result = Status.SUCCESS,
    timestamp = 1610672400000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1610326800100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1610672400100,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1610672422494,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1610668800000,
            date = "2021-01-15 00:00:00 +0800"
        )
    )
)
val cfrExecution8 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 8,
    duration = 621278,
    result = Status.SUCCESS,
    timestamp = 1610758800000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1610758800100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1610759086584,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1610759108978,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1610755200000,
            date = "2021-01-16 00:00:00 +0800"
        )
    )
)
val cfrExecution9 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 9,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1611104400000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1611104400100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1611104686684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.FAILED,
            startTimeMillis = 1611104686784,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1610755200000,
            date = "2021-01-20 00:00:00 +0800"
        )
    )
)
val cfrExecution10 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 10,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1611190800000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1611190800100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1611191086684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.FAILED,
            startTimeMillis = 1611191109078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1611187200000,
            date = "2021-01-21 00:00:00 +0800"
        )
    )
)
val cfrExecution11 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 11,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1611277200000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1611277200100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1611277230200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1611277235200,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1611273600000,
            date = "2020-1-22 00:00:00 +0800"
        )
    )
)
val cfrExecution12 = Execution(
    pipelineId = "6012505c42fbb8439fc08b21",
    number = 12,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1611878400000,
    url = "http://localhost:8001/job/4km-cfr/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1611878400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.SUCCESS,
            startTimeMillis = 1611878430100,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to UAT",
            status = Status.SUCCESS,
            startTimeMillis = 1611878435200,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1611792000000,
            date = "2020-1-28 00:00:00 +0800"
        )
    )
)

val cfrBuilds = listOf(
    cfrExecution1,
    cfrExecution2,
    cfrExecution3,
    cfrExecution4,
    cfrExecution5,
    cfrExecution6,
    cfrExecution7,
    cfrExecution8,
    cfrExecution9,
    cfrExecution10,
    cfrExecution11,
    cfrExecution12
)
