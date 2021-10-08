package metrik.fixtures

import metrik.project.domain.model.Execution
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status

val dfPipeline = Pipeline(
    id = "601cbb3425c1392117aa053b",
    projectId = "601cbae825c1392117aa0429",
    name = "df",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-df/",
    type = PipelineType.JENKINS
)

val dfExecution1 = Execution(
    pipelineId = "601cbb3425c1392117aa053b",
    number = 1,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1609462800000,
    url = "http://localhost:8001/job/4km-backend/1/",
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
val dfExecution2 = Execution(
    pipelineId = "601cbb3425c1392117aa053b",
    number = 1,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1609488000000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609488000000,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.FAILED,
            startTimeMillis = 1609466700000,
            durationMillis = 5000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1609466400000,
            date = "2021-01-01 10:00:00 +0800"
        )
    )
)
val dfExecution3 = Execution(
    pipelineId = "601cbb3425c1392117aa053b",
    number = 1,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1609477200000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            name = "build",
            status = Status.SUCCESS,
            startTimeMillis = 1609477200000,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            name = "Deploy to DEV",
            status = Status.OTHER,
            startTimeMillis = 1609477500000,
            durationMillis = 5000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1609473600000,
            date = "2021-01-01 12:00:00 +0800"
        )
    )
)
