package metrik.fixtures

import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status


val ltPipeline1 = Pipeline(
    id = "6012505c42fbb8439fc08e17",
    projectId = "601cbae825c1392117aa0429",
    name = "mlt1",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mlt1/",
    type = PipelineType.JENKINS
)
val ltPipeline2 = Pipeline(
    id = "6012505c42fbb8439fc08e16",
    projectId = "601cbae825c1392117aa0429",
    name = "mlt2",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mlt2/",
    type = PipelineType.JENKINS
)
val ltPipeline3 = Pipeline(
    id = "6012505c42fbb8439fc08e15",
    projectId = "601cbae825c1392117aa0429",
    name = "mlt3",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mlt3/",
    type = PipelineType.JENKINS
)
val ltPipeline4 = Pipeline(
    id = "6012505c42fbb8439fc08e14",
    projectId = "601cbae825c1392117aa0429",
    name = "mlt4",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mlt4/",
    type = PipelineType.JENKINS
)
val ltPipeline5 = Pipeline(
    id = "6012505c42fbb8439fc08e13",
    projectId = "601cbae825c1392117aa0429",
    name = "mlt5",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mlt5/",
    type = PipelineType.JENKINS
)
val ltPipeline6 = Pipeline(
    id = "6012505c42fbb8439fc08e12",
    projectId = "601cbae825c1392117aa0429",
    name = "mlt6",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mlt6/",
    type = PipelineType.JENKINS
)

val ltBuild1 = Build(
    pipelineId = "6012505c42fbb8439fc08e17",
    number = 1,
    duration = 7200000,
    result = Status.SUCCESS,
    timestamp = 1598284800000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598284800000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.SUCCESS,
            name = "deploy to prod",
            startTimeMillis = 1598288400000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112345",
            msg = "this is a message",
            timestamp = 1597852800000,
            date = "2020-08-20 00:00:00  +0800"
        )
    )
)
val ltBuild2 = Build(
    pipelineId = "6012505c42fbb8439fc08e16",
    number = 1,
    duration = 7200000,
    result = Status.FAILED,
    timestamp = 1598284800000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598284800000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.FAILED,
            name = "deploy to prod",
            startTimeMillis = 1598288400000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112345",
            msg = "this is a message",
            timestamp = 1595613600000,
            date = "2020-07-25 02:00:00  +0800"
        )
    )
)
val ltBuild3 = Build(
    pipelineId = "6012505c42fbb8439fc08e15",
    number = 1,
    duration = 7200000,
    result = Status.OTHER,
    timestamp = 1598284800000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598284800000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.OTHER,
            name = "deploy to prod",
            startTimeMillis = 1598288400000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112345",
            msg = "this is a message",
            timestamp = 1597852800000,
            date = "2020-08-20 00:00:00  +0800"
        )
    )
)
val ltBuild4 = Build(
    pipelineId = "6012505c42fbb8439fc08e14",
    number = 1,
    duration = 7200000,
    result = Status.SUCCESS,
    timestamp = 1598284800000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598284800000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.SUCCESS,
            name = "deploy to prod",
            startTimeMillis = 1598288400000,
            durationMillis = 3600000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = emptyList()
)
val ltBuild5 = Build(
    pipelineId = "6012505c42fbb8439fc08e13",
    number = 1,
    duration = 200,
    result = Status.FAILED,
    timestamp = 1598284800000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598284800000,
            durationMillis = 100,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.FAILED,
            name = "deploy to prod",
            startTimeMillis = 1598284800100,
            durationMillis = 100,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112345",
            msg = "this is a message",
            timestamp = 1598284800000,
            date = "2020-08-20 00:00:00  +0800"
        )
    )
)
val ltBuild6 = Build(
    pipelineId = "6012505c42fbb8439fc08e13",
    number = 2,
    duration = 200,
    result = Status.SUCCESS,
    timestamp = 1598292000000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598292000000,
            durationMillis = 100,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.SUCCESS,
            name = "deploy to prod",
            startTimeMillis = 1598292000100,
            durationMillis = 100,
            pauseDurationMillis = 0
        )
    ),
    changeSets = emptyList()
)
val ltBuild7 = Build(
    pipelineId = "6012505c42fbb8439fc08e12",
    number = 1,
    duration = 200,
    result = Status.SUCCESS,
    timestamp = 1598292000000,
    url = "http://localhost:8001/job/4km-backend/1/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1598292000000,
            durationMillis = 100,
            pauseDurationMillis = 0
        ), Stage(
            status = Status.SUCCESS,
            name = "deploy to prod",
            startTimeMillis = 1598292000100,
            durationMillis = 100,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112345",
            msg = "this is a message",
            timestamp = 1597852800000,
            date = "2020-08-20 00:00:00  +0800"
        ),
        Commit(

            commitId = "b9b775059d120a0dbf09fd40d2becea69a112341",
            msg = "this is a message",
            timestamp = 1597939200000,
            date = "2020-08-21 00:00:00  +0800"
        ),
        Commit(

            commitId = "b9b775059d120a0dbf09fd40d2becea69a112342",
            msg = "this is a message",
            timestamp = 1598284800000,
            date = "2020-08-25 00:00:00  +0800"
        )
    )
)

val ltPipelines = listOf(
    ltPipeline1,
    ltPipeline2,
    ltPipeline3,
    ltPipeline4,
    ltPipeline5,
    ltPipeline6
)
val ltBuilds = listOf(
    ltBuild1,
    ltBuild2,
    ltBuild3,
    ltBuild4,
    ltBuild5,
    ltBuild6,
    ltBuild7,
)