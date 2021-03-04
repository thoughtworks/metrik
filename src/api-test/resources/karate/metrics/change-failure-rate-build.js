db.build.insertMany([
    {
        "_id": ObjectId("603740e89234a9398a8dba34"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 1,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1609462800000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1609462800100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1609462830200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1609462835300),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 31st",
                "timestamp": NumberLong(1609408800000),
                "date": "2020-12-31 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba35"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 2,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1609549200000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1609549200100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1609549203100),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1609549208200),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112401",
                "msg": "commit on 2nd",
                "timestamp": NumberLong(1609545600000),
                "date": "2021-01-2 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba36"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 3,
        "duration": 50000,
        "result": "OTHER",
        "timestamp": NumberLong(1609808400000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1609808400100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1609808430100),
                "durationMillis": 10000,
                "pauseDurationMillis": 0
            },
            {
                "status": "OTHER",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1609808440200),
                "durationMillis": 10000,
                "pauseDurationMillis": 0
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112402",
                "msg": "commit on 5th Jan",
                "timestamp": NumberLong(1609804800000),
                "date": "2021-01-5 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba37"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 4,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1609894800000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1609894800100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1609894830200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1609894835300),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112403",
                "msg": "commit on 6th",
                "timestamp": NumberLong(1609891200000),
                "date": "2021-01-6 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba38"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 5,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1609981200000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1609981200100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1609981486684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1609981509078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112404",
                "msg": "commit on 7th",
                "timestamp": NumberLong(1609977600000),
                "date": "2021-01-7 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba39"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 6,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1610326800000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1610326800100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1610327086684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1610327109078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112348",
                "msg": "commit on 11th",
                "timestamp": NumberLong(1610323200000),
                "date": "2021-01-11 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba3a"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 7,
        "duration": 621278,
        "result": "SUCCESS",
        "timestamp": NumberLong(1610672400000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1610326800100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1610672400100),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1610672422494),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 15th",
                "timestamp": NumberLong(1610668800000),
                "date": "2021-01-15 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba3b"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 8,
        "duration": 621278,
        "result": "SUCCESS",
        "timestamp": NumberLong(1610758800000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1610758800100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1610759086584),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1610759108978),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 16th",
                "timestamp": NumberLong(1610755200000),
                "date": "2021-01-16 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba3c"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 9,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1611104400000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1611104400100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1611104686684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1611104686784),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 20th",
                "timestamp": NumberLong(1610755200000),
                "date": "2021-01-20 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba3d"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 10,
        "duration": 621278,
        "result": "FAILED",
        "timestamp": NumberLong(1611190800000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1611190800100),
                "durationMillis": 286484,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1611191086684),
                "durationMillis": 22294,
                "pauseDurationMillis": 0
            },
            {
                "status": "FAILED",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1611191109078),
                "durationMillis": 300119,
                "pauseDurationMillis": 299981
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112405",
                "msg": "commit on 21st",
                "timestamp": NumberLong(1611187200000),
                "date": "2021-01-21 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba3e"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 11,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1611277200000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1611277200100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1611277230200),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1611277235200),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 22nd",
                "timestamp": NumberLong(1611273600000),
                "date": "2020-1-22 00:00:00 +0800"
            }
        ]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dba3f"),
        "pipelineId": "6012505c42fbb8439fc08b21",
        "number": 12,
        "duration": 40000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1611878400000),
        "url": "http://localhost:8001/job/4km-cfr/",
        "stages": [
            {
                "status": "SUCCESS",
                "name": "build",
                "startTimeMillis": NumberLong(1611878400100),
                "durationMillis": 30000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to DEV",
                "startTimeMillis": NumberLong(1611878430100),
                "durationMillis": 5000,
                "pauseDurationMillis": 0
            },
            {
                "status": "SUCCESS",
                "name": "Deploy to UAT",
                "startTimeMillis": NumberLong(1611878435200),
                "durationMillis": 5000,
                "pauseDurationMillis": 2000
            }
        ],
        "changeSets": [
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112400",
                "msg": "commit on 28th",
                "timestamp": NumberLong(1611792000000),
                "date": "2020-1-28 00:00:00 +0800"
            }
        ]
    }
])