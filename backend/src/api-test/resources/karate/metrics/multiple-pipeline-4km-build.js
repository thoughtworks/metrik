db.build.insertMany([
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c8f"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 1,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1578281700000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578281700100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578281986684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578282009078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112348",
                "msg": "commit on 6th",
                "timestamp": NumberLong(1578281400000),
                "date": "2021-01-6 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c90"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 2,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1578353350000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578353400000),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578353400100),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578353422494),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 7th",
                "timestamp": NumberLong(1578351600000),
                "date": "2021-01-7 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c91"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 3,
        "duration": 621278,
        "result": "SUCCESS",
        "timestamp": NumberLong(1578483000000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578483000100),
                "durationMillis": 1800000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578484800200),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578484822594),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 8th",
                "timestamp": NumberLong(1578474000000),
                "date": "2021-01-8 00:00:00 +0800"
            },
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112543",
                "msg": "commit on 8th",
                "timestamp": NumberLong(1578475800000),
                "date": "2021-01-8 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c92"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 4,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1578532200000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578532200100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578532486784),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578532509178),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 9th",
                "timestamp": NumberLong(1578531900000),
                "date": "2021-01-9 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c93"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 5,
        "duration": 621278,
        "result": "OTHER",
        "timestamp": NumberLong(1578618000000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578618000100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578618286684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "OTHER",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578618309078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 10st",
                "timestamp": NumberLong(1578618000000),
                "date": "2021-01-10 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c94"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 6,
        "duration": 40000,
        "result": "OTHER",
        "timestamp": NumberLong(1578704400000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578704400100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578704430200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "OTHER",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578704435300),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 11nd",
                "timestamp": NumberLong(1578700800000),
                "date": "2020-1-11 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c95"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 7,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1578783600000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578783600100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578783630200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578783635300),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 12th",
                "timestamp": NumberLong(1578783600000),
                "date": "2020-1-12 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c96"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 8,
        "duration": 40000,
        "result": "FAILED",
        "timestamp": NumberLong(1578873600000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578873600100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578873630200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578873635300),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 13th",
                "timestamp": NumberLong(1578870000000),
                "date": "2020-1-13 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cb5f4ed956ea8e05c97"),
        "pipelineId": "600a701221048076f92c4e43",
        "number": 9,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1578963600000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578963600100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1578963630200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1578963635300),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 14th",
                "timestamp": NumberLong(1578960000000),
                "date": "2020-1-14 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cc3f4ed956ea8e05c98"),
        "pipelineId": "601a2f129deac2220dd07570",
        "number": 1,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1578281700000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578281700100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "API Test",
                "startTimeMillis": NumberLong(1578281986684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to Staging",
                "startTimeMillis": NumberLong(1578282009078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112348",
                "msg": "commit on 6th",
                "timestamp": NumberLong(1578281400000),
                "date": "2021-01-6 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cc3f4ed956ea8e05c99"),
        "pipelineId": "601a2f129deac2220dd07570",
        "number": 2,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1610672400000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578353400000),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "API Test",
                "startTimeMillis": NumberLong(1578353400100),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to Staging",
                "startTimeMillis": NumberLong(1578353422494),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 7th",
                "timestamp": NumberLong(1578351600000),
                "date": "2021-01-7 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cc3f4ed956ea8e05c9a"),
        "pipelineId": "601a2f129deac2220dd07570",
        "number": 3,
        "duration": 621278,
        "result": "SUCCESS",
        "timestamp": NumberLong(1578483000000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578483000100),
                "durationMillis": 1800000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "API Test",
                "startTimeMillis": NumberLong(1578484800200),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to Staging",
                "startTimeMillis": NumberLong(1578484822594),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 8th",
                "timestamp": NumberLong(1578474000000),
                "date": "2021-01-8 00:00:00 +0800"
            },
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112543",
                "msg": "commit on 8th",
                "timestamp": NumberLong(1578475800000),
                "date": "2021-01-8 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cc3f4ed956ea8e05c9b"),
        "pipelineId": "601a2f129deac2220dd07570",
        "number": 4,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1578551400000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578551400100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "API Test",
                "startTimeMillis": NumberLong(1578551686684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to Staging",
                "startTimeMillis": NumberLong(1578551708998),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112348",
                "msg": "commit on 9th",
                "timestamp": NumberLong(1578547800000),
                "date": "2021-01-9 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cc3f4ed956ea8e05c9c"),
        "pipelineId": "601a2f129deac2220dd07570",
        "number": 5,
        "duration": 621278,
        "result": "OTHER",
        "timestamp": NumberLong(1578618000000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578618000100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "API Test",
                "startTimeMillis": NumberLong(1578618286684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "OTHER",
                "name": "Deploy to Staging",
                "startTimeMillis": NumberLong(1578618309078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 10th",
                "timestamp": NumberLong(1578618000000),
                "date": "2021-01-10 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("60221cc3f4ed956ea8e05c9d"),
        "pipelineId": "601a2f129deac2220dd07570",
        "number": 6,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1578704400000),
        "url": "http://localhost:8001/job/4km-mttr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1578704400100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "API Test",
                "startTimeMillis": NumberLong(1578704430200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to Staging",
                "startTimeMillis": NumberLong(1578704435300),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 11st",
                "timestamp": NumberLong(1578700800000),
                "date": "2020-1-11 00:00:00 +0800"
            }
        ]
    }
])