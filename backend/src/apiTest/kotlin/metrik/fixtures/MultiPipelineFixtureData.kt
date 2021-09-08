package metrik.fixtures

import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status

val multiPipeline1 = Pipeline(
    id = "600a701221048076f92c4e43",
    projectId = "601cbae825c1392117aa0429",
    name = "multiPipeline1",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-multi-pipeline-test-1/",
    type = PipelineType.JENKINS
)
val multiPipeline2 = Pipeline(
    id = "601a2f129deac2220dd07570",
    projectId = "601cbae825c1392117aa0429",
    name = "multiPipeline2",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-multi-pipeline-test-2/",
    type = PipelineType.JENKINS
)

val multiPipelineBuild1 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 1,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1578281700000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578281700100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578281986684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1578282009078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112348",
            timestamp = 1578281400000,
            date = "2021-01-6 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild2 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 2,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1578353350000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578353400000,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578353400100,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1578353422494,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578351600000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild3 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 3,
    duration = 621278,
    result = Status.SUCCESS,
    timestamp = 1578483000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578483000100,
            durationMillis = 1800000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578484800200,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1578484822594,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578474000000,
            date = "2021-01-8 00:00:00 +0800"
        ),
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112543",
            timestamp = 1578475800000,
            date = "2021-01-8 00:00:00 +0800"
        )
    )
)

val multiPipelineBuild4 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 4,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1578532200000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578532200100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578532486784,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1578532509178,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578531900000,
            date = "2021-01-9 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild5 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 5,
    duration = 621278,
    result = Status.OTHER,
    timestamp = 1578618000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578618000100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578618286684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1578618309078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578618000000,
            date = "2021-01-10 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild6 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 6,
    duration = 40000,
    result = Status.OTHER,
    timestamp = 1578704400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578704400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578704430200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1578704435300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1578700800000,
            date = "2020-1-11 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild7 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 7,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1578783600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578783600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578783630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1578783635300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1578783600000,
            date = "2020-1-12 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild8 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 8,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1578873600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578873600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578873630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1578873635300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1578870000000,
            date = "2020-1-13 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild9 = Build(
    pipelineId = "600a701221048076f92c4e43",
    number = 9,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1578963600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578963600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578963630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1578963635300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1578960000000,
            date = "2020-1-14 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild10 = Build(
    pipelineId = "601a2f129deac2220dd07570",
    number = 10,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1578281700000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578281700100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "API Test",
            startTimeMillis = 1578281986684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to Staging",
            startTimeMillis = 1578282009078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112348",
            timestamp = 1578281400000,
            date = "2021-01-6 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild11 = Build(
    pipelineId = "601a2f129deac2220dd07570",
    number = 11,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1610672400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578353400000,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "API Test",
            startTimeMillis = 1578353400100,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to Staging",
            startTimeMillis = 1578353422494,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578351600000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild12 = Build(
    pipelineId = "601a2f129deac2220dd07570",
    number = 12,
    duration = 621278,
    result = Status.SUCCESS,
    timestamp = 1578483000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578483000100,
            durationMillis = 1800000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "API Test",
            startTimeMillis = 1578484800200,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to Staging",
            startTimeMillis = 1578484822594,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578474000000,
            date = "2021-01-8 00:00:00 +0800"
        ),
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112543",
            timestamp = 1578475800000,
            date = "2021-01-8 00:00:00 +0800"
        )
    )
)

val multiPipelineBuild13 = Build(
    pipelineId = "601a2f129deac2220dd07570",
    number = 13,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1578551400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578551400100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "API Test",
            startTimeMillis = 1578551686684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to Staging",
            startTimeMillis = 1578551708998,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112348",
            timestamp = 1578547800000,
            date = "2021-01-9 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild14 = Build(
    pipelineId = "601a2f129deac2220dd07570",
    number = 14,
    duration = 621278,
    result = Status.OTHER,
    timestamp = 1578618000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578618000100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "API Test",
            startTimeMillis = 1578618286684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to Staging",
            startTimeMillis = 1578618309078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            timestamp = 1578618000000,
            date = "2021-01-10 00:00:00 +0800"
        )
    )
)
val multiPipelineBuild15 = Build(
    pipelineId = "601a2f129deac2220dd07570",
    number = 15,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1578704400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578704400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "API Test",
            startTimeMillis = 1578704430200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to Staging",
            startTimeMillis = 1578704435300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            timestamp = 1578700800000,
            date = "2020-1-11 00:00:00 +0800"
        )
    )
)

val multiPipelineBuilds = listOf(
    multiPipelineBuild1,
    multiPipelineBuild2,
    multiPipelineBuild3,
    multiPipelineBuild4,
    multiPipelineBuild5,
    multiPipelineBuild6,
    multiPipelineBuild7,
    multiPipelineBuild8,
    multiPipelineBuild9,
    multiPipelineBuild10,
    multiPipelineBuild11,
    multiPipelineBuild12,
    multiPipelineBuild13,
    multiPipelineBuild14,
    multiPipelineBuild15
)
