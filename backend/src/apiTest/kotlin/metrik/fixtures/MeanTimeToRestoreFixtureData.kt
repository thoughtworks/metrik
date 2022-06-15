package metrik.fixtures

import metrik.project.domain.model.Commit
import metrik.project.domain.model.Execution
import metrik.project.domain.model.PipelineConfiguration
import metrik.project.domain.model.PipelineType
import metrik.project.domain.model.Stage
import metrik.project.domain.model.Status

val mttrPipeline1 = PipelineConfiguration(
    id = "6018c32f42fbb8439fc08b24",
    projectId = "601cbae825c1392117aa0429",
    name = "pipeline1",
    username = "E9fnMY3UGE6Oms35JzLGgQ==",
    credential = "FVSR/5o1BYh6dJQBeQaNvQ==",
    url = "http://localhost:8001/job/4km-mttr/",
    type = PipelineType.JENKINS
)

val mttrExecution1 = Execution(
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
            timestamp = 1577807999000,
            date = "2019-12-31 00:00:00 +0800"
        )
    )
)
val mttrExecution2 = Execution(
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
            timestamp = 1577923200000,
            date = "2021-01-2 00:00:00 +0800"
        )
    )
)
val mttrExecution3 = Execution(
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
            timestamp = 1578009600000,
            date = "2021-01-3 00:00:00 +0800"
        )
    )
)
val mttrExecution4 = Execution(
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
            timestamp = 1578096000000,
            date = "2021-01-4 00:00:00 +0800"
        )
    )
)
val mttrExecution5 = Execution(
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
            timestamp = 1578100800000,
            date = "2021-01-4 00:00:00 +0800"
        )
    )
)
val mttrExecution6 = Execution(
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
            timestamp = 1578281400000,
            date = "2021-01-6 00:00:00 +0800"
        )
    )
)
val mttrExecution7 = Execution(
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
            timestamp = 1578351600000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val mttrExecution8 = Execution(
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
            timestamp = 1578358800000,
            date = "2021-01-7 00:00:00 +0800"
        )
    )
)
val mttrExecution9 = Execution(
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
            timestamp = 1578531900000,
            date = "2021-01-9 00:00:00 +0800"
        )
    )
)
val mttrExecution10 = Execution(
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
            timestamp = 1578618000000,
            date = "2021-01-10 00:00:00 +0800"
        )
    )
)
val mttrExecution11 = Execution(
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
            timestamp = 1578700800000,
            date = "2020-1-11 00:00:00 +0800"
        )
    )
)
val mttrExecution12 = Execution(
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
            timestamp = 1578783600000,
            date = "2020-1-12 00:00:00 +0800"
        )
    )
)
val mttrExecution13 = Execution(
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
            timestamp = 1578870000000,
            date = "2020-1-13 00:00:00 +0800"
        )
    )
)
val mttrExecution14 = Execution(
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
            timestamp = 1578960000000,
            date = "2020-1-14 00:00:00 +0800"
        )
    )
)
val mttrExecution15 = Execution(
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
            timestamp = 1579046400000,
            date = "2020-1-15 00:00:00 +0800"
        )
    )
)
val mttrExecution16 = Execution(
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
            timestamp = 1579132800000,
            date = "2020-1-16 00:00:00 +0800"
        )
    )
)
val mttrExecution17 = Execution(
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
            timestamp = 1579219200000,
            date = "2020-1-17 00:00:00 +0800"
        )
    )
)
val mttrExecution18 = Execution(
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
            timestamp = 1579305600000,
            date = "2020-1-18 00:00:00 +0800"
        )
    )
)
val mttrExecution19 = Execution(
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
            timestamp = 1579392000000,
            date = "2020-1-19 00:00:00 +0800"
        )
    )
)
val mttrExecution20 = Execution(
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
            timestamp = 1579478400000,
            date = "2020-1-20 00:00:00 +0800"
        )
    )
)
val mttrExecution21 = Execution(
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
            timestamp = 1579564800000,
            date = "2020-1-21 00:00:00 +0800"
        )
    )
)
val mttrExecution22 = Execution(
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
            timestamp = 1579651200000,
            date = "2020-1-22 00:00:00 +0800"
        )
    )
)
val mttrExecution23 = Execution(
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
            timestamp = 1579737600000,
            date = "2020-1-23 00:00:00 +0800"
        )
    )
)
val mttrExecution24 = Execution(
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
            timestamp = 1579824000000,
            date = "2020-1-24 00:00:00 +0800"
        )
    )
)
val mttrExecution25 = Execution(
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
            timestamp = 1579910400000,
            date = "2020-1-25 00:00:00 +0800"
        )
    )
)
val mttrExecution26 = Execution(
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
            timestamp = 1580688000000,
            date = "2020-2-3 00:00:00 +0800"
        )
    )
)
val mttrExecution27 = Execution(
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
            timestamp = 1580774400000,
            date = "2020-2-4 00:00:00 +0800"
        )
    )
)
val mttrExecution28 = Execution(
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
            timestamp = 1580860800000,
            date = "2020-2-5 00:00:00 +0800"
        )
    )
)

val mttrBuilds = listOf(
    mttrExecution1,
    mttrExecution2,
    mttrExecution3,
    mttrExecution4,
    mttrExecution5,
    mttrExecution6,
    mttrExecution7,
    mttrExecution8,
    mttrExecution9,
    mttrExecution10,
    mttrExecution11,
    mttrExecution12,
    mttrExecution13,
    mttrExecution14,
    mttrExecution15,
    mttrExecution16,
    mttrExecution17,
    mttrExecution18,
    mttrExecution19,
    mttrExecution20,
    mttrExecution21,
    mttrExecution22,
    mttrExecution23,
    mttrExecution24,
    mttrExecution25,
    mttrExecution26,
    mttrExecution27,
    mttrExecution28,
)
