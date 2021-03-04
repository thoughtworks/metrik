db.build.insertMany([
    {
    "_id" : ObjectId("601cbb7625c1392117aa042b"),
    "pipelineId" : "601cbb3425c1392117aa053b",
    "number" : 1.0,
    "duration" : 40000.0,
    "result" : "SUCCESS",
    "timestamp" : NumberLong(1609462800000),
    "url" : "http://localhost:8001/job/4km-backend/1/",
    "stages" : [
        {
            "status" : "SUCCESS",
            "name" : "build",
            "startTimeMillis" : NumberLong(1609462800100),
            "durationMillis" : 30000.0,
            "pauseDurationMillis" : 0.0
        },
        {
            "status" : "SUCCESS",
            "name" : "Deploy to DEV",
            "startTimeMillis" : NumberLong(1609462830200),
            "durationMillis" : 5000.0,
            "pauseDurationMillis" : 0.0
        },
        {
            "status" : "SUCCESS",
            "name" : "Deploy to UAT",
            "startTimeMillis" : NumberLong(1609462835300),
            "durationMillis" : 5000.0,
            "pauseDurationMillis" : 2000.0
        }
    ],
    "changeSets" : [
        {
            "commitId" : "b9b775059d120a0dbf09fd40d2becea69a112400",
            "msg" : "commit on 31st",
            "timestamp" : NumberLong(1609408800000),
            "date" : "2020-12-31 00:00:00 +0800"
        }
    ]
},
    {
        "_id" : ObjectId("6020ea7025c1392117aa042c"),
        "pipelineId" : "601cbb3425c1392117aa053b",
        "number" : 1.0,
        "duration" : 40000.0,
        "result" : "FAILED",
        "timestamp" : NumberLong(1609488000000),
        "url" : "http://localhost:8001/job/4km-backend/1/",
        "stages" : [
            {
                "status" : "SUCCESS",
                "name" : "build",
                "startTimeMillis" : NumberLong(1609488000000),
                "durationMillis" : 30000.0,
                "pauseDurationMillis" : 0.0
            },
            {
                "status" : "FAILED",
                "name" : "Deploy to DEV",
                "startTimeMillis" : NumberLong(1609466700000),
                "durationMillis" : 5000.0,
                "pauseDurationMillis" : 0.0
            }
        ],
        "changeSets" : [
            {
                "commitId" : "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg" : "commit on 1st",
                "timestamp" : NumberLong(1609466400000),
                "date" : "2021-01-01 10:00:00 +0800"
            }
        ]
    },
    {
        "_id" : ObjectId("6020efd725c1392117aa042d"),
        "pipelineId" : "601cbb3425c1392117aa053b",
        "number" : 1.0,
        "duration" : 40000.0,
        "result" : "FAILED",
        "timestamp" : NumberLong(1609477200000),
        "url" : "http://localhost:8001/job/4km-backend/1/",
        "stages" : [
            {
                "status" : "SUCCESS",
                "name" : "build",
                "startTimeMillis" : NumberLong(1609477200000),
                "durationMillis" : 30000.0,
                "pauseDurationMillis" : 0.0
            },
            {
                "status" : "OTHER",
                "name" : "Deploy to DEV",
                "startTimeMillis" : NumberLong(1609477500000),
                "durationMillis" : 5000.0,
                "pauseDurationMillis" : 0.0
            }
        ],
        "changeSets" : [
            {
                "commitId" : "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg" : "commit on 1st",
                "timestamp" : NumberLong(1609473600000),
                "date" : "2021-01-01 12:00:00 +0800"
            }
        ]
    }
]);