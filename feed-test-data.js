use 4-Key-Metrics;

// remove all test data before insert
db.build.remove({"_id": ObjectId("601cbb7625c1392117aa042b")});
db.build.remove({"_id": ObjectId("6020ea7025c1392117aa042c")});
db.build.remove({"_id": ObjectId("6020efd725c1392117aa042d")});
db.pipeline.remove({"_id": ObjectId("601cbb3425c1392117aa042a")});
db.dashboard.remove({"_id": ObjectId("601cbae825c1392117aa0429")});

//insert test data 
db.dashboard.insert({
    "_id" : ObjectId("601cbae825c1392117aa0429"),
    "name" : "4-key",
    "_class" : "fourkeymetrics.dashboard.model.Dashboard",
    "synchronizationTimes" : "1580709600000"
});
db.pipeline.insert({
    "_id" : ObjectId("601cbb3425c1392117aa042a"),
    "dashboardId" : "601cbae825c1392117aa0429",
    "name" : "dfservice",
    "username" : "4km",
    "credential" : "4000km",
    "url" : "http://localhost:8001/job/4km-df/",
    "type" : "JENKINS",
    "_class" : "fourkeymetrics.dashboard.model.Pipeline"
});
db.build.insertMany([{
    "_id" : ObjectId("601cbb7625c1392117aa042b"),
    "pipelineId" : "601cbb3425c1392117aa042a",
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
    "pipelineId" : "601cbb3425c1392117aa042a",
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
    "pipelineId" : "601cbb3425c1392117aa042a",
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