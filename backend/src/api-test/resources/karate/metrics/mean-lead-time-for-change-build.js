db.build.insertMany([
    {
        "_id": ObjectId("603740e89234a9398a8dcc12"),
        "pipelineId": "6012505c42fbb8439fc08e17",
        "number": 1,
        "duration": 7200000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1598284800000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598284800000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }, {
            "status": "SUCCESS",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598288400000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }],
        "changeSets": [{
            "commitId": "b9b775059d120a0dbf09fd40d2becea69a112345",
            "msg": "this is a message",
            "timestamp": NumberLong(1597852800000),
            "date": "2020-08-20 00:00:00  +0800"
        }]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dcc13"),
        "pipelineId": "6012505c42fbb8439fc08e16",
        "number": 1,
        "duration": 7200000,
        "result": "FAILED",
        "timestamp": NumberLong(1598284800000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598284800000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }, {
            "status": "FAILED",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598288400000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }],
        "changeSets": [{
            "commitId": "b9b775059d120a0dbf09fd40d2becea69a112345",
            "msg": "this is a message",
            "timestamp": NumberLong(1595613600000),
            "date": "2020-07-25 02:00:00  +0800"
        }]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dcc14"),
        "pipelineId": "6012505c42fbb8439fc08e15",
        "number": 1,
        "duration": 7200000,
        "result": "OTHER",
        "timestamp": NumberLong(1598284800000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598284800000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }, {
            "status": "OTHER",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598288400000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }],
        "changeSets": [{
            "commitId": "b9b775059d120a0dbf09fd40d2becea69a112345",
            "msg": "this is a message",
            "timestamp": NumberLong(1597852800000),
            "date": "2020-08-20 00:00:00  +0800"
        }]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dcc15"),
        "pipelineId": "6012505c42fbb8439fc08e14",
        "number": 1,
        "duration": 7200000,
        "result": "SUCCESS",
        "timestamp": NumberLong(1598284800000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598284800000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }, {
            "status": "SUCCESS",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598288400000),
            "durationMillis": 3600000,
            "pauseDurationMillis": 0
        }],
        "changeSets": []
    },
    {
        "_id": ObjectId("603740e89234a9398a8dcc16"),
        "pipelineId": "6012505c42fbb8439fc08e13",
        "number": 1,
        "duration": 200,
        "result": "FAILED",
        "timestamp": NumberLong(1598284800000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598284800000),
            "durationMillis": 100,
            "pauseDurationMillis": 0
        }, {
            "status": "FAILED",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598284800100),
            "durationMillis": 100,
            "pauseDurationMillis": 0
        }],
        "changeSets": [{
            "commitId": "b9b775059d120a0dbf09fd40d2becea69a112345",
            "msg": "this is a message",
            "timestamp": NumberLong(1598284800000),
            "date": "2020-08-20 00:00:00  +0800"
        }]
    },
    {
        "_id": ObjectId("603740e89234a9398a8dcc17"),
        "pipelineId": "6012505c42fbb8439fc08e13",
        "number": 2,
        "duration": 200,
        "result": "SUCCESS",
        "timestamp": NumberLong(1598292000000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598292000000),
            "durationMillis": 100,
            "pauseDurationMillis": 0
        }, {
            "status": "SUCCESS",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598292000100),
            "durationMillis": 100,
            "pauseDurationMillis": 0
        }],
        "changeSets": []
    },
    {
        "_id": ObjectId("603740e89234a9398a8dcc18"),
        "pipelineId": "6012505c42fbb8439fc08e12",
        "number": 1,
        "duration": 200,
        "result": "SUCCESS",
        "timestamp": NumberLong(1598292000000),
        "url": "http://localhost:8001/job/4km-backend/1/",
        "stages": [{
            "status": "SUCCESS",
            "name": "build",
            "startTimeMillis": NumberLong(1598292000000),
            "durationMillis": 100,
            "pauseDurationMillis": 0
        }, {
            "status": "SUCCESS",
            "name": "deploy to prod",
            "startTimeMillis": NumberLong(1598292000100),
            "durationMillis": 100,
            "pauseDurationMillis": 0
        }],
        "changeSets": [{
            "commitId": "b9b775059d120a0dbf09fd40d2becea69a112345",
            "msg": "this is a message",
            "timestamp": NumberLong(1597852800000),
            "date": "2020-08-20 00:00:00  +0800"
        },
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112341",
                "msg": "this is a message",
                "timestamp": NumberLong(1597939200000),
                "date": "2020-08-21 00:00:00  +0800"
            },
            {
                "commitId": "b9b775059d120a0dbf09fd40d2becea69a112342",
                "msg": "this is a message",
                "timestamp": NumberLong(1598284800000),
                "date": "2020-08-25 00:00:00  +0800"
            }]
    }
])