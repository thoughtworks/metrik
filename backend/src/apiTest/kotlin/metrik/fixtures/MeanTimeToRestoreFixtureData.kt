package metrik.fixtures

import metrik.project.domain.model.Build
import metrik.project.domain.model.Commit
import metrik.project.domain.model.Pipeline
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status

val mttrPipeline1 = Pipeline(
    id = "6018c32f42fbb8439fc08b24",
    projectId = "601cbae825c1392117aa0429",
    name = "pipeline1",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mttr/",
    type = PipelineType.JENKINS
)

val mttrBuild1 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 1,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1577840400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1577840400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1577840430100,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1577840435200,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 31st",
            timestamp = 1577807999000,
            date = "2019-12-31 00:00:00 +0800"
        )
    )
)
val mttrBuild2 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 2,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1577926800000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1577926800100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1577926830100,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1577926835200,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112401",
            msg = "commit on 2nd",
            timestamp = 1577923200000,
            date = "2021-01-2 00:00:00 +0800"
        )
    )
)
val mttrBuild3 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 3,
    duration = 50000,
    result = Status.SUCCESS,
    timestamp = 1578013200000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578013200100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578013230200,
            durationMillis = 10000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1578013240300,
            durationMillis = 10000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112402",
            msg = "commit on 3rd Jan",
            timestamp = 1578009600000,
            date = "2021-01-3 00:00:00 +0800"
        )
    )
)
val mttrBuild4 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 4,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1578099600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578099600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578099630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1578099635300,
            durationMillis = 5000,
            pauseDurationMillis = 0
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112403",
            msg = "commit on 4th",
            timestamp = 1578096000000,
            date = "2021-01-4 00:00:00 +0800"
        )
    )
)
val mttrBuild5 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 5,
    duration = 621278,
    result = Status.SUCCESS,
    timestamp = 1578101400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578101400100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578101686684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1578101709078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112404",
            msg = "commit on 4th",
            timestamp = 1578100800000,
            date = "2021-01-4 00:00:00 +0800"
        )
    )
)
val mttrBuild6 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 6,
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
            msg = "commit on 6th",
            timestamp = 1578281400000,
            date = "2021-01-6 00:00:00 +0800"
        )
    )
)
val mttrBuild7 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 7,
    duration = 621278,
    result = Status.FAILED,
    timestamp = 1578351600000,
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
            msg = "commit on 7th",
            timestamp = 1578351600000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val mttrBuild8 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 8,
    duration = 621278,
    result = Status.SUCCESS,
    timestamp = 1578359100000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1578359100100,
            durationMillis = 286484,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1578359386684,
            durationMillis = 22294,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1578359409078,
            durationMillis = 300119,
            pauseDurationMillis = 299981
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112405",
            msg = "commit on 7th",
            timestamp = 1578358800000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val mttrBuild9 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 9,
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
            msg = "commit on 9th",
            timestamp = 1578531900000,
            date = "2021-01-9 00:00:00 +0800"
        )
    )
)
val mttrBuild10 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 10,
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
            msg = "commit on 10st",
            timestamp = 1578618000000,
            date = "2021-01-10 00:00:00 +0800"
        )
    )
)
val mttrBuild11 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 11,
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
            msg = "commit on 11nd",
            timestamp = 1578700800000,
            date = "2020-1-11 00:00:00 +0800"
        )
    )
)
val mttrBuild12 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 12,
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
            msg = "commit on 12th",
            timestamp = 1578783600000,
            date = "2020-1-12 00:00:00 +0800"
        )
    )
)
val mttrBuild13 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 13,
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
            msg = "commit on 13th",
            timestamp = 1578870000000,
            date = "2020-1-13 00:00:00 +0800"
        )
    )
)
val mttrBuild14 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 14,
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
            msg = "commit on 14th",
            timestamp = 1578960000000,
            date = "2020-1-14 00:00:00 +0800"
        )
    )
)
val mttrBuild15 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 15,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1579050000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579050000100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579050030200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1579050035300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 15th",
            timestamp = 1579046400000,
            date = "2020-1-15 00:00:00 +0800"
        )
    )
)
val mttrBuild16 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 16,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1579136400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579136400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579136430200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1579136435300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 16th",
            timestamp = 1579132800000,
            date = "2020-1-16 00:00:00 +0800"
        )
    )
)
val mttrBuild17 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 17,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1579222800000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579222800100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579222830200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1579222835300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 17th",
            timestamp = 1579219200000,
            date = "2020-1-17 00:00:00 +0800"
        )
    )
)
val mttrBuild18 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 18,
    duration = 40000,
    result = Status.OTHER,
    timestamp = 1579309200000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579309200100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579309230200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1579309235300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 18th",
            timestamp = 1579305600000,
            date = "2020-1-18 00:00:00 +0800"
        )
    )
)
val mttrBuild19 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 19,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1579395600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579395600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579395630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1579395635300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 19th",
            timestamp = 1579392000000,
            date = "2020-1-19 00:00:00 +0800"
        )
    )
)
val mttrBuild20 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 20,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1579482000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579482000100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579482030200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1579482035300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 20th",
            timestamp = 1579478400000,
            date = "2020-1-20 00:00:00 +0800"
        )
    )
)
val mttrBuild21 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 21,
    duration = 40000,
    result = Status.OTHER,
    timestamp = 1579568400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579568400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579568430200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1579568435300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 21st",
            timestamp = 1579564800000,
            date = "2020-1-21 00:00:00 +0800"
        )
    )
)
val mttrBuild22 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 22,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1579654800000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579654800100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579654830200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1579654835300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 22nd",
            timestamp = 1579651200000,
            date = "2020-1-22 00:00:00 +0800"
        )
    )
)
val mttrBuild23 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 23,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1579741200000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579741200100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579741230200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1579741235300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 23rd",
            timestamp = 1579737600000,
            date = "2020-1-23 00:00:00 +0800"
        )
    )
)
val mttrBuild24 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 24,
    duration = 40000,
    result = Status.OTHER,
    timestamp = 1579827600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579827600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579827630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1579827635200,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 24th",
            timestamp = 1579824000000,
            date = "2020-1-24 00:00:00 +0800"
        )
    )
)
val mttrBuild25 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 25,
    duration = 40000,
    result = Status.FAILED,
    timestamp = 1579914000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1579914000100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1579914030200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.FAILED,
            name = "Deploy to UAT",
            startTimeMillis = 1579914035300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 25th",
            timestamp = 1579910400000,
            date = "2020-1-25 00:00:00 +0800"
        )
    )
)
val mttrBuild26 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 26,
    duration = 40000,
    result = Status.OTHER,
    timestamp = 1580691600000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1580691600100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1580691630200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1580691635300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 3rd Feb",
            timestamp = 1580688000000,
            date = "2020-2-3 00:00:00 +0800"
        )
    )
)
val mttrBuild27 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 27,
    duration = 40000,
    result = Status.OTHER,
    timestamp = 1580778000000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1580778000100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1580778030200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.OTHER,
            name = "Deploy to UAT",
            startTimeMillis = 1580778035300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 4th Feb",
            timestamp = 1580774400000,
            date = "2020-2-4 00:00:00 +0800"
        )
    )
)
val mttrBuild28 = Build(
    pipelineId = "6018c32f42fbb8439fc08b24",
    number = 28,
    duration = 40000,
    result = Status.SUCCESS,
    timestamp = 1580864400000,
    url = "http://localhost:8001/job/4km-mttr/",
    stages = listOf(
        Stage(
            status = Status.SUCCESS,
            name = "build",
            startTimeMillis = 1580864400100,
            durationMillis = 30000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to DEV",
            startTimeMillis = 1580864430200,
            durationMillis = 5000,
            pauseDurationMillis = 0
        ),
        Stage(
            status = Status.SUCCESS,
            name = "Deploy to UAT",
            startTimeMillis = 1580864435300,
            durationMillis = 5000,
            pauseDurationMillis = 2000
        )
    ),
    changeSets = listOf(
        Commit(
            commitId = "b9b775059d120a0dbf09fd40d2becea69a112400",
            msg = "commit on 5th Feb",
            timestamp = 1580860800000,
            date = "2020-2-5 00:00:00 +0800"
        )
    )
)

val mttrBuilds = listOf(
    mttrBuild1,
    mttrBuild2,
    mttrBuild3,
    mttrBuild4,
    mttrBuild5,
    mttrBuild6,
    mttrBuild7,
    mttrBuild8,
    mttrBuild9,
    mttrBuild10,
    mttrBuild11,
    mttrBuild12,
    mttrBuild13,
    mttrBuild14,
    mttrBuild15,
    mttrBuild16,
    mttrBuild17,
    mttrBuild18,
    mttrBuild19,
    mttrBuild20,
    mttrBuild21,
    mttrBuild22,
    mttrBuild23,
    mttrBuild24,
    mttrBuild25,
    mttrBuild26,
    mttrBuild27,
    mttrBuild28,
)
